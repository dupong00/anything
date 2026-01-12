package com.example.anything.vote.internal.service;

import com.example.anything.common.BusinessException;
import com.example.anything.vote.application.port.MenuModulePort;
import com.example.anything.vote.dto.BallotBoxRequest;
import com.example.anything.vote.dto.MenuResponseDto;
import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.domain.Status;
import com.example.anything.vote.internal.domain.VoteErrorCode;
import com.example.anything.vote.internal.domain.VoteOption;
import com.example.anything.vote.internal.domain.VoteRecord;
import com.example.anything.vote.internal.repository.BallotBoxRepository;
import com.example.anything.vote.internal.repository.VoteOptionRepository;
import com.example.anything.vote.internal.repository.VoteRecordRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {
    private final MenuModulePort menuModulePort;
    private final BallotBoxRepository ballotBoxRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;

    @Transactional
    public Long createBallotBox(BallotBoxRequest request){
        Set<Long> uniqueIds = new HashSet<>(request.menuList());

        List<MenuResponseDto> menus = menuModulePort.getMenusByIds(new ArrayList<>(uniqueIds));

        if (menus.size() != uniqueIds.size()) {
            throw new BusinessException(VoteErrorCode.MENU_NOT_FOUND);
        }

        BallotBox ballotBox = BallotBox.create(
                request.groupId(),
                request.title(),
                request.deadline(),
                request.latitude(),
                request.longitude(),
                request.locationName()
        );

        for (MenuResponseDto menu : menus) {
            ballotBox.addVoteOption(menu.id(), menu.name());
        }

        ballotBoxRepository.save(ballotBox);

        return ballotBox.getId();
    }

    @Transactional
    public Long castVote(Long userId, Long ballotBoxId, List<Long> menus){
        if (menus == null || menus.isEmpty()){
            throw new BusinessException(VoteErrorCode.INVALID_VOTE_COUNT);
        }

        if (menus.size() > 3){
            throw new BusinessException(VoteErrorCode.EXCEED_MAX_COUNT);
        }

        if (voteRecordRepository.existsByMemberIdAndBallotBoxId(userId, ballotBoxId)) {
            throw new BusinessException(VoteErrorCode.ALREADY_VOTED);
        }

        BallotBox ballotBox = ballotBoxRepository.findById(ballotBoxId)
                .orElseThrow(() -> new BusinessException(VoteErrorCode.BALLOT_BOX_NOT_FOUND));

        if (ballotBox.getStatus() != Status.ACTIVE){
            throw new BusinessException(VoteErrorCode.BALLOT_BOX_NOT_ACTIVE);
        }

        for (Long menu : menus) {
            VoteOption voteOption = voteOptionRepository.findByBallotBox_IdAndMenuId(ballotBoxId, menu)
                            .orElseThrow(() -> new BusinessException(VoteErrorCode.VOTE_OPTION_NOT_FOUND));

            VoteRecord voteRecord = VoteRecord.create(userId, ballotBox, voteOption);

            voteOption.addCount();

            voteRecordRepository.save(voteRecord);
        }
        return ballotBoxId;
    }
}