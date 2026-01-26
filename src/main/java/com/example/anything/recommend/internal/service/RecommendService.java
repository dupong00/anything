package com.example.anything.recommend.internal.service;

import com.example.anything.common.BusinessException;
import com.example.anything.menu.application.port.MenuModulePort;
import com.example.anything.menu.application.port.MenuResponseDto;
import com.example.anything.recommend.infrastructure.naver.LocalSearchResponse;
import com.example.anything.recommend.infrastructure.naver.NaverClient;
import com.example.anything.recommend.infrastructure.naver.NaverMapClient;
import com.example.anything.recommend.infrastructure.naver.ReverseGeocodingResponse;
import com.example.anything.recommend.internal.domain.RecommendErrorCode;
import com.example.anything.recommend.internal.domain.Restaurant;
import com.example.anything.recommend.internal.domain.RestaurantMenu;
import com.example.anything.recommend.internal.repository.RestaurantMenuRepository;
import com.example.anything.recommend.internal.repository.RestaurantRepository;
import com.example.anything.vote.application.port.VoteModulePort;
import com.example.anything.vote.application.port.WinnerMenuInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuRepository restaurantMenuRepository;
    private final VoteModulePort voteModulePort;
    private final MenuModulePort menuModulePort;
    private final NaverClient naverClient;
    private final NaverMapClient naverMapClient;

    @Transactional
    public void generateRecommend(Long ballotBoxId){
        if (restaurantMenuRepository.existsByBallotBoxId(ballotBoxId)){
            return;
        }

        WinnerMenuInfo winners = voteModulePort.getWinnerMenus(ballotBoxId);

        List<Long> winnerMenus = winners.getWinnerMenus();

        List<MenuResponseDto> menus = menuModulePort.getMenusByIds(winnerMenus);

        ReverseGeocodingResponse addressInfo = naverMapClient.reverseGeocoding(
                winners.getLongitude(),
                winners.getLatitude()
        );

        if (addressInfo == null || addressInfo.getResults() == null || addressInfo.getResults().isEmpty()) {
            throw new BusinessException(RecommendErrorCode.ADDRESS_NOT_FOUND);
        }

        var region =  addressInfo.getResults().getFirst().getRegion();
        String area1 = region.getArea1().getName();
        String area2 = region.getArea2().getName();
        String area3 = region.getArea3().getName();
        String localName = String.format("%s %s %s", area1, area2, area3);

        for (MenuResponseDto menu : menus) {
            LocalSearchResponse localSearchResponse = naverClient.searchLocal(localName, menu.name());

            if (localSearchResponse.getTotal() > 0) {
                searchConverter(localSearchResponse, ballotBoxId, menu.id(), menu.name());
            }
        }
    }

    @Transactional
    public void searchConverter(LocalSearchResponse response, Long ballotBoxId, Long targetMenuId, String menuName) {
        response.getItems().forEach(item -> {
            Restaurant restaurant = getOrCreateRestaurant(item);

            RestaurantMenu restaurantMenu = RestaurantMenu.create(
                    targetMenuId,
                    ballotBoxId,
                    menuName,
                    restaurant
            );

            restaurantMenuRepository.save(restaurantMenu);
        });
    }

    private Restaurant getOrCreateRestaurant(LocalSearchResponse.LocalItem item) {
        try {
            return restaurantRepository.findByApiId(item.getLink())
                    .orElseGet(() -> restaurantRepository.save(
                            Restaurant.create(
                                    cleanTitle(item.getTitle()),
                                    item.getLink(),
                                    item.getCategory(),
                                    item.getRoadAddress(),
                                    item.getAddress(),
                                    item.getMapx() / 10000000.0,
                                    item.getMapy() / 10000000.0
                            )
                    ));
        } catch (DataIntegrityViolationException e) {
            return restaurantRepository.findByApiId(item.getLink())
                    .orElseThrow(() -> new BusinessException(RecommendErrorCode.RETRY_FAILED));
        }
    }

    public List<RestaurantMenu> getRecommend(Long ballotBoxId){
        return restaurantMenuRepository.findByBallotBoxId(ballotBoxId);
    }

    private String cleanTitle(String title) {
        return title.replaceAll("<[^>]*>", "");
    }
}
