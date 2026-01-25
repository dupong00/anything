package com.example.anything.recommend.infrastructure.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.Getter;

@Data
@Schema(description = "Naver 지도 좌표로 동네 검색 응답 정보")
public class ReverseGeocodingResponse {
    private List<Result> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Result {
        private String name;
        private Region region;
        private Land land;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Region {
        @Schema(description = "시/도", example = "경기도")
        private Area area1;

        @Schema(description = "시/군/구", example = "동두천시")
        private Area area2;

        @Schema(description = "읍/면/동", example = "지행동")
        private Area area3;

        @Data
        public static class Area {
            private String name;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Land {
        @Schema(description = "지번 본번호 또는 도로명 주소 번호")
        private String number1;

        @Schema(description = "건물명")
        private Addition0 addition0;

        @Data
        public static class Addition0 {
            private String value; // 실제 건물 이름
        }
    }
}
