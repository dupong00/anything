package com.example.anything.vote.internal.repository;

import com.example.anything.vote.internal.domain.VoteOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    Optional<VoteOption> findByBallotBoxIdAndMenuId(Long ballotBoxId, Long menuId);
}
