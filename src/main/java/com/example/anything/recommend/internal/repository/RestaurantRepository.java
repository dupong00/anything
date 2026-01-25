package com.example.anything.recommend.internal.repository;

import com.example.anything.recommend.internal.domain.Restaurant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByApiId(String apiId);
}
