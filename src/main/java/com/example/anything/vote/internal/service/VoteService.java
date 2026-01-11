package com.example.anything.vote.internal.service;

import com.example.anything.common.BusinessException;
import com.example.anything.vote.application.port.MenuModulePort;
import com.example.anything.vote.dto.BallotBoxRequest;
import com.example.anything.vote.dto.MenuResponseDto;
import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.domain.VoteErrorCode;
import com.example.anything.vote.internal.repository.BallotBoxRepository;
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

        return ballotBox.getBallotBoxId();
    }
}