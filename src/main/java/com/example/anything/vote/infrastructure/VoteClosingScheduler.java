package com.example.anything.vote.infrastructure;

import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.domain.Status;
import com.example.anything.vote.internal.repository.BallotBoxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VoteClosingScheduler {
    private final BallotBoxRepository ballotBoxRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void closeExpiredVotes() {
        List<BallotBox> expiredVotes = ballotBoxRepository.findAllByStatusAndDeadlineBefore(
                Status.ACTIVE, LocalDateTime.now()
        );

        expiredVotes.forEach(BallotBox::checkAndClose);
    }
}