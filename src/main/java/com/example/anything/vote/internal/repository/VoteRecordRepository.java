package com.example.anything.vote.internal.repository;

import com.example.anything.vote.internal.domain.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    Boolean existsByMemberIdAndBallotBoxId(Long memberId, Long ballotBoxId);
}
