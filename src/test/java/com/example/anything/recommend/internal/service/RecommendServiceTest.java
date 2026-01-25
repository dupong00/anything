package com.example.anything.recommend.internal.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.example.anything.menu.application.port.MenuModulePort;
import com.example.anything.menu.application.port.MenuResponseDto;
import com.example.anything.recommend.internal.domain.RestaurantMenu;
import com.example.anything.recommend.internal.repository.RestaurantRepository;
import com.example.anything.vote.application.port.VoteModulePort;
import com.example.anything.vote.application.port.WinnerMenuInfo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class RecommendServiceTest {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private RestaurantRepository restaurantRepository;

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

        List<RestaurantMenu> results = recommendService.createRecommend(ballotBoxId);

        assertThat(results).isNotEmpty();

        RestaurantMenu savedMapping = results.getFirst();
        assertThat(savedMapping.getMenuName()).isEqualTo("돈가스");

        boolean exists = restaurantRepository.findByApiId(savedMapping.getRestaurant().getApiId()).isPresent();
        assertThat(exists).isTrue();
    }
}