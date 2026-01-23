package com.example.anything.recommend.infrastructure.naver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NaverClientTest {
    @Autowired
    private NaverClient naverClient;

    @Test
    @DisplayName("네이버 지역 검색 API 호출 테스트 - 결과가 String으로 잘 오는지 확인")
    void searchLocalTest() {
        // given
        String localName = "양주시";
        String menuName = "돈가스";

        // when
        String result = naverClient.searchLocal(localName, menuName);

        // then
        System.out.println("API Response: " + result);
        assertThat(result).isNotNull();
        assertThat(result).contains("items");
    }
}