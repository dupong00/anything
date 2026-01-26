package com.example.anything.recommend.internal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant")
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;
    private String roadAddress;
    private String address;

    private double longitude;
    private double latitude;

    @Column(unique = true, nullable = false)
    private String apiId;

    public static Restaurant create(String name, String apiId, String category, String roadAddress, String address, double longitude, double latitude) {
        return Restaurant.builder()
                .name(name)
                .apiId(apiId)
                .category(category)
                .roadAddress(roadAddress)
                .address(address)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }
}
