package com.example.anything.recommend.internal.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "restaurant_menu",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ballot_box_menu_restaurant",
                        columnNames = {"ballotBoxId", "menuId", "restaurant_id"} // 복합 유니크 키 설정
                )
        }
)
public class RestaurantMenu {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long menuId;

    private Long ballotBoxId;

    private String menuName;

    private int exposureCount;

    private int clickCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    public static RestaurantMenu create(Long menuId, Long ballotBoxId,String menuName, Restaurant restaurant) {
        return RestaurantMenu.builder()
                .menuId(menuId)
                .ballotBoxId(ballotBoxId)
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
