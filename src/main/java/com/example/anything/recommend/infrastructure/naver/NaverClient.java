package com.example.anything.recommend.infrastructure.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    public String searchLocal(String localName, String menuName) {
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

        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
    }
}