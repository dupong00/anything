package com.example.anything.recommend.infrastructure.naver;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "Naver 지역 검색 응답 정보")
public class LocalSearchResponse {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<LocalItem> items; // 검색 결과 리스트

    @Data
    public static class LocalItem {
        @Schema(description = "식당 이름", example = "대왕<b>돈까스</b>&amp;귀엽지롱")
        private String title;

        @Schema(description = "상세 정보 URL", example = "http://www.instagram.com/cute_caron")
        private String link;

        @Schema(description = "분류", example = "음식점>일식>돈가스")
        private String category;

        @Schema(description = "설명")
        private String description;

        @Schema(description = "지번 주소", example = "경기도 동두천시 지행동 718-1 두손프라자 304호")
        private String address;

        @Schema(description = "도로명 주소", example = "경기도 동두천시 중앙로 116 두손프라자 304호")
        private String roadAddress;

        @Schema(description = "경도", example = "1270526226")
        private long mapx;

        @Schema(description = "위도", example = "378918234")
        private long mapy;
    }
}
