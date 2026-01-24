package com.example.anything.recommend.infrastructure.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverClient {

    @Value("${naver.api.dev.client-id}")
    private String clientId;

    @Value("${naver.api.dev.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public NaverClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LocalSearchResponse searchLocal(String localName, String menuName) {
        String fullQuery = localName + " " + menuName;

        String url = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/local.json")
                .queryParam("query", fullQuery)
                .queryParam("display", 10)
                .queryParam("sort", "comment")
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.set("Accept", "application/json");

        ResponseEntity<LocalSearchResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        LocalSearchResponse.class
                );

        if (response.getBody() == null) {
            throw new RuntimeException("Naver 지역 검색 결과가 비었습니다.");
        }

        return response.getBody();
    }
}