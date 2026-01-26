package com.example.anything.recommend.dto;

import com.example.anything.recommend.internal.domain.RestaurantMenu;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendResultResponse {
    private Long restaurantId;
    private String restaurantName;
    private String menuName;
    private String category;
    private String roadAddress;
    private String link;
    private double longitude;
    private double latitude;

    public static RecommendResultResponse from(RestaurantMenu entity) {
        var restaurant = entity.getRestaurant();
        return RecommendResultResponse.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .menuName(entity.getMenuName())
                .category(restaurant.getCategory())
                .roadAddress(restaurant.getRoadAddress())
                .link(restaurant.getApiId())
                .longitude(restaurant.getLongitude())
                .latitude(restaurant.getLatitude())
                .build();
    }
}
