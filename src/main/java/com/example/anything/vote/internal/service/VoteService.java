package com.example.anything.vote.internal.service;

import com.example.anything.vote.application.port.MenuModulePort;
import com.example.anything.vote.dto.BallotBoxRequest;
import com.example.anything.vote.dto.MenuResponseDto;
import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.repository.BallotBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {
    private final MenuModulePort menuModulePort;
    private final BallotBoxRepository ballotBoxRepository;

    @Transactional
    public Long createBallotBox(BallotBoxRequest request){
        List<MenuResponseDto> menus = menuModulePort.getMenusByIds(request.menuList());

        if (menus.size() != request.menuList().size()) {
            throw new IllegalArgumentException("존재하지 않는 메뉴가 포함되어 있습니다.");
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