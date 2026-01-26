package com.example.anything.recommend.internal.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.example.anything.menu.application.port.MenuModulePort;
import com.example.anything.menu.application.port.MenuResponseDto;
import com.example.anything.recommend.internal.domain.RestaurantMenu;
import com.example.anything.recommend.internal.repository.RestaurantMenuRepository;
import com.example.anything.recommend.internal.repository.RestaurantRepository;
import com.example.anything.vote.application.port.VoteModulePort;
import com.example.anything.vote.application.port.WinnerMenuInfo;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RecommendServiceTest {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantMenuRepository restaurantMenuRepository;

    @MockitoBean
    private VoteModulePort voteModulePort;

    @MockitoBean
    private MenuModulePort menuModulePort;

    @Test
    void createRecommend() {
        Long ballotBoxId = 1L;

        WinnerMenuInfo winnerInfo = WinnerMenuInfo.builder()
                .longitude(127.0526226)
                .latitude(37.8918234)
                .winnerMenus(List.of(100L))
                .build();

        MenuResponseDto menuDto = new MenuResponseDto(100L, "돈가스");

        given(voteModulePort.getWinnerMenus(ballotBoxId)).willReturn(winnerInfo);
        given(menuModulePort.getMenusByIds(anyList())).willReturn(List.of(menuDto));

        recommendService.generateRecommend(ballotBoxId);

        List<RestaurantMenu> savedResults = restaurantMenuRepository.findByBallotBoxId(ballotBoxId);

        assertThat(savedResults).isNotEmpty();
        RestaurantMenu firstResult = savedResults.getFirst();
        assertThat(firstResult.getMenuName()).isEqualTo("돈가스");
        assertThat(firstResult.getBallotBoxId()).isEqualTo(ballotBoxId);

        boolean exists = restaurantRepository.findByApiId(firstResult.getRestaurant().getApiId()).isPresent();
        assertThat(exists).isTrue();
    }
}