package com.example.anything.vote.internal.repository;

import com.example.anything.vote.internal.domain.VoteOption;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VoteOption v WHERE v.ballotBox.id = :ballotBoxId AND v.menuId = :menuId")
    Optional<VoteOption> findByBallotBox_IdAndMenuId(Long ballotBoxId, Long menuId);
}
