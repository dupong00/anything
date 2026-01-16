package com.example.anything.vote.internal.repository;

import com.example.anything.vote.Status;
import com.example.anything.vote.internal.domain.BallotBox;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BallotBoxRepository extends JpaRepository<BallotBox, Long> {
    List<BallotBox> findAllByStatusAndDeadlineBefore(Status status, LocalDateTime deadline);
    Optional<BallotBox> findById(Long ballotBoxId);

    List<BallotBox> findAllByGroupIdInAndStatusNot(List<Long> groupIds, Status status);
    List<BallotBox> findAllByGroupIdInAndStatus(List<Long> groupIds, Status status);
}
