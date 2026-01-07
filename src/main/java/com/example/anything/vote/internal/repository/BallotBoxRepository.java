package com.example.anything.vote.internal.repository;

import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BallotBoxRepository extends JpaRepository<BallotBox, Long> {
    List<BallotBox> findAllByStatusAndDeadlineBefore(Status status, LocalDateTime deadline);
}
