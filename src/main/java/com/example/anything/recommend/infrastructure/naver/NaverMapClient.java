package com.example.anything.recommend.infrastructure.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverMapClient {
    @Value("${naver.api.cloud.client-id}")
    private String cloudClientId;

    @Value("${naver.api.cloud.client-secret}")
    private String cloudClientSecret;

    private final  RestTemplate restTemplate;

    public NaverMapClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ReverseGeocodingResponse reverseGeocoding(double longitude, double latitude) {
        String url = UriComponentsBuilder
                .fromUriString("https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc?")
                .queryParam("coords", longitude + "," + latitude)
                .queryParam("sourcecrs", "epsg:4326")
                .queryParam("orders", "addr,roadaddr")
                .queryParam("output", "json")
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-api-key-id", cloudClientId);
        headers.set("x-ncp-apigw-api-key", cloudClientSecret);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ReverseGeocodingResponse.class
        ).getBody();
    }
}
