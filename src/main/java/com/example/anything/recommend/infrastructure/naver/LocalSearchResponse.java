package com.example.anything.recommend.infrastructure.naver;

import java.util.List;
import lombok.Data;

@Data
public class LocalSearchResponse {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<LocalItem> items; // 검색 결과 리스트

    @Data
    public static class LocalItem {
        private String title;       // 식당 이름 (<b> 태그 포함 가능)
        private String link;        // 상세 정보 URL
        private String category;    // 분류
        private String description; // 설명
        private String address;     // 지번 주소
        private String roadAddress; // 도로명 주소
        private long mapx;        // 경도(Longitude, WGS84)
        private long mapy;        // 위도(Latitude, WGS84)
    }
}
