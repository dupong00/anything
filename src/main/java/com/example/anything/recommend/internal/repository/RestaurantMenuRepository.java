package com.example.anything.recommend.internal.repository;

import com.example.anything.recommend.internal.domain.RestaurantMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantMenuRepository extends JpaRepository<RestaurantMenu, Long> {
}
