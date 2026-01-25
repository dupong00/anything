package com.example.anything.recommend.internal.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantMenu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long menuId;

    private String menuName;

    private int exposureCount;

    private int clickCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    public static RestaurantMenu create(Long menuId, String menuName, Restaurant restaurant) {
        return RestaurantMenu.builder()
                .menuId(menuId)
                .menuName(menuName)
                .restaurant(restaurant)
                .build();
    }
    public void incrementExposure() {
        this.exposureCount++;
    }

    public void incrementClick() {
        this.clickCount++;
    }
}
