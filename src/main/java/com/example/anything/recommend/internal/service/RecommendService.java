package com.example.anything.recommend.internal.service;

import com.example.anything.menu.application.port.MenuModulePort;
import com.example.anything.menu.application.port.MenuResponseDto;
import com.example.anything.recommend.infrastructure.naver.LocalSearchResponse;
import com.example.anything.recommend.infrastructure.naver.NaverClient;
import com.example.anything.recommend.infrastructure.naver.NaverMapClient;
import com.example.anything.recommend.infrastructure.naver.ReverseGeocodingResponse;
import com.example.anything.recommend.internal.domain.Restaurant;
import com.example.anything.recommend.internal.domain.RestaurantMenu;
import com.example.anything.recommend.internal.repository.RestaurantMenuRepository;
import com.example.anything.recommend.internal.repository.RestaurantRepository;
import com.example.anything.vote.application.port.VoteModulePort;
import com.example.anything.vote.application.port.WinnerMenuInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
    public List<RestaurantMenu> createRecommend(Long ballotBoxId){
        WinnerMenuInfo winners = voteModulePort.getWinnerMenus(ballotBoxId);

        List<Long> winnerMenus = winners.getWinnerMenus();

        List<MenuResponseDto> menus = menuModulePort.getMenusByIds(winnerMenus);


        ReverseGeocodingResponse addressInfo = naverMapClient.reverseGeocoding(
                winners.getLongitude(),
                winners.getLatitude()
        );

        var region =  addressInfo.getResults().getFirst().getRegion();
        String area1 = region.getArea1().getName();
        String area2 = region.getArea2().getName();
        String area3 = region.getArea3().getName();
        String localName = String.format("%s %s %s", area1, area2, area3);

        List<RestaurantMenu> totalResults = new ArrayList<>();

        for (MenuResponseDto menu : menus) {
            LocalSearchResponse localSearchResponse = naverClient.searchLocal(localName, menu.name());

            if (localSearchResponse.getTotal() > 0) {
                List<RestaurantMenu> savedRestaurantMenu = searchConverter(localSearchResponse, menu.id(), menu.name());
                totalResults.addAll(savedRestaurantMenu);
            }
        }

        return totalResults;
    }

    @Transactional
    public List<RestaurantMenu> searchConverter(LocalSearchResponse response, Long targetMenuId, String menuName) {
        return response.getItems().stream()
                .map(item -> {
                    String cleanedTitle = cleanTitle(item.getTitle());
                    double longitude = item.getMapx() / 10000000.0;
                    double latitude = item.getMapy() / 10000000.0;
                    Restaurant restaurant = restaurantRepository.findByApiId(item.getLink())
                            .orElseGet(() -> restaurantRepository.save(
                                    Restaurant.create(
                                            cleanedTitle,
                                            item.getLink(),
                                            item.getCategory(),
                                            item.getRoadAddress(),
                                            item.getAddress(),
                                            longitude,
                                            latitude
                                    )
                            ));


                    RestaurantMenu restaurantMenu = RestaurantMenu.create(
                            targetMenuId,
                            menuName,
                            restaurant
                    );

                    return restaurantMenuRepository.save(restaurantMenu);
                })
                .collect(Collectors.toList());
    }

    private String cleanTitle(String title) {
        return title.replaceAll("<[^>]*>", "");
    }
}
