package com.example.anything.recommend.internal.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.anything.menu.application.port.MenuModulePort;
import com.example.anything.menu.application.port.MenuResponseDto;
import com.example.anything.recommend.infrastructure.naver.LocalSearchResponse;
import com.example.anything.recommend.infrastructure.naver.NaverClient;
import com.example.anything.recommend.infrastructure.naver.NaverMapClient;
import com.example.anything.recommend.infrastructure.naver.ReverseGeocodingResponse;
import com.example.anything.recommend.internal.domain.RestaurantMenu;
import com.example.anything.recommend.internal.repository.RestaurantMenuRepository;
import com.example.anything.recommend.internal.repository.RestaurantRepository;
import com.example.anything.vote.application.port.VoteModulePort;
import com.example.anything.vote.application.port.WinnerMenuInfo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
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
    private RestaurantMenuRepository restaurantMenuRepository;

    @MockitoBean
    private VoteModulePort voteModulePort;

    @MockitoBean
    private MenuModulePort menuModulePort;
    
    @MockitoBean
    private NaverClient naverClient;

    @MockitoBean
    private NaverMapClient naverMapClient;

    @Test
    @DisplayName("추천 생성 테스트: 외부 API를 모킹하여 네트워크 호출 없이 기능을 검증한다")
    void generateRecommendWithMock() {
        // given
        Long ballotBoxId = 1L;
        
        WinnerMenuInfo winnerInfo = WinnerMenuInfo.builder()
                .longitude(127.0).latitude(37.0)
                .winnerMenus(List.of(100L)).build();
        given(voteModulePort.getWinnerMenus(ballotBoxId)).willReturn(winnerInfo);
        given(menuModulePort.getMenusByIds(anyList())).willReturn(List.of(new MenuResponseDto(100L, "돈가스")));

        ReverseGeocodingResponse mockAddress = createMockAddress();
        given(naverMapClient.reverseGeocoding(anyDouble(), anyDouble())).willReturn(mockAddress);

        LocalSearchResponse mockSearch = createMockSearch();
        given(naverClient.searchLocal(anyString(), anyString())).willReturn(mockSearch);

        // when
        recommendService.generateRecommend(ballotBoxId);

        // then
        List<RestaurantMenu> results = restaurantMenuRepository.findByBallotBoxId(ballotBoxId);
        assertThat(results).isNotEmpty();
        assertThat(results.getFirst().getMenuName()).isEqualTo("돈가스");
    }

    private ReverseGeocodingResponse createMockAddress() {
        ReverseGeocodingResponse response = new ReverseGeocodingResponse();

        ReverseGeocodingResponse.Region region = new ReverseGeocodingResponse.Region();
        region.setArea1(new ReverseGeocodingResponse.Region.Area());
        region.getArea1().setName("경기도");
        region.setArea2(new ReverseGeocodingResponse.Region.Area());
        region.getArea2().setName("동두천시");
        region.setArea3(new ReverseGeocodingResponse.Region.Area());
        region.getArea3().setName("지행동");

        ReverseGeocodingResponse.Result result = new ReverseGeocodingResponse.Result();
        result.setRegion(region);

        response.setResults(List.of(result));
        return response;
    }

    private LocalSearchResponse createMockSearch() {
        LocalSearchResponse response = new LocalSearchResponse();
        response.setTotal(1);

        LocalSearchResponse.LocalItem item = new LocalSearchResponse.LocalItem();
        item.setTitle("<b>대왕돈까스</b>");
        item.setLink("https://naver.com/123");
        item.setCategory("음식점>경양식");
        item.setRoadAddress("경기도 동두천시...");
        item.setMapx(1270526226);
        item.setMapy(378918234);

        response.setItems(List.of(item));
        return response;
    }
}