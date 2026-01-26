package com.example.anything.vote.infrastructure;

import com.example.anything.common.BusinessException;
import com.example.anything.vote.application.port.VoteModulePort;
import com.example.anything.vote.application.port.WinnerMenuInfo;
import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.domain.VoteErrorCode;
import com.example.anything.vote.internal.repository.BallotBoxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VoteModuleAdapter implements VoteModulePort {
    public final BallotBoxRepository ballotBoxRepository;
    @Override
    @Transactional(readOnly = true)
    public WinnerMenuInfo getWinnerMenus(Long ballotBoxId) {

        BallotBox ballotBox = ballotBoxRepository.findById(ballotBoxId)
                .orElseThrow(() -> new BusinessException(VoteErrorCode.BALLOT_BOX_NOT_FOUND));

        List<Long> winnerMenuIds = ballotBox.getWinnerMenuIds();

        return WinnerMenuInfo.builder()
                .longitude(ballotBox.getLongitude())
                .latitude(ballotBox.getLatitude())
                .winnerMenus(winnerMenuIds)
                .build();
    }
}
