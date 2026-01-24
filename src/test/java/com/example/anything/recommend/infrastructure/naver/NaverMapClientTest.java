package com.example.anything.recommend.infrastructure.naver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NaverMapClientTest {

    @Autowired
    private NaverMapClient naverMapClient;

    @Test
    @DisplayName("네이버 역지오코딩 API 호출 테스트 - 실제 좌표로 주소 변환 확인")
    void reverseGeocodingTest() {
        double longitude = 127.0526226;
        double latitude = 37.8918234;

        ReverseGeocodingResponse response = naverMapClient.reverseGeocoding(longitude, latitude);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getResults()).isNotNull();
        assertThat(response.getResults()).isNotEmpty();

        ReverseGeocodingResponse.Region region = response.getResults().getFirst().getRegion();
        String area1 = region.getArea1().getName(); // 경기도
        String area2 = region.getArea2().getName(); // 동두천시
        String area3 = region.getArea3().getName(); // 지행동

        System.out.println("변환된 주소: " + area1 + " " + area2 + " " + area3);

        assertThat(area1).isEqualTo("경기도");
        assertThat(area2).contains("동두천시");
    }
}