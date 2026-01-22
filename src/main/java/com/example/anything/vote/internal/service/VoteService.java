package com.example.anything.vote.internal.service;

import com.example.anything.common.BusinessException;
import com.example.anything.vote.Status;
import com.example.anything.group.application.port.GroupModulePort;
import com.example.anything.menu.application.port.MenuModulePort;
import com.example.anything.vote.dto.BallotBoxDetailResponse;
import com.example.anything.vote.dto.BallotBoxRequest;
import com.example.anything.vote.dto.BallotBoxesResponse;
import com.example.anything.vote.dto.CategorySelection;
import com.example.anything.menu.application.port.MenuResponseDto;
import com.example.anything.vote.internal.domain.BallotBox;
import com.example.anything.vote.internal.domain.VoteErrorCode;
import com.example.anything.vote.internal.domain.VoteOption;
import com.example.anything.vote.internal.domain.VoteRecord;
import com.example.anything.vote.internal.repository.BallotBoxRepository;
import com.example.anything.vote.internal.repository.VoteOptionRepository;
import com.example.anything.vote.internal.repository.VoteRecordRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoteService {
    private final MenuModulePort menuModulePort;
    private final GroupModulePort groupModulePort;
    private final BallotBoxRepository ballotBoxRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;

    @Transactional
    public Long createBallotBox(BallotBoxRequest request, Long memberId){
        if (request.selections() == null || request.selections().isEmpty()) {
            throw new BusinessException(VoteErrorCode.MENU_NOT_FOUND);
        }

        Set<MenuResponseDto> finalMenus = new HashSet<>();

        for (CategorySelection selection : request.selections()) {
            List<MenuResponseDto> resolved = fetchMenus(selection);

            if (!selection.menuIds().contains(0L)) {
                long requestedUniqueCount = selection.menuIds().stream().distinct().count();
                if (resolved.size() != (int) requestedUniqueCount) {
                    throw new BusinessException(VoteErrorCode.MENU_NOT_FOUND);
                }
            }

            if (resolved.isEmpty()) {
                throw new BusinessException(VoteErrorCode.MENU_NOT_FOUND);
            }

            finalMenus.addAll(resolved);
        }

        BallotBox ballotBox = BallotBox.create(
                request.groupId(),
                memberId,
                request.title(),
                request.deadline(),
                request.latitude(),
                request.longitude(),
                request.locationName()
        );

        for (MenuResponseDto menu : finalMenus) {
            ballotBox.addVoteOption(menu.id(), menu.name());
        }

        ballotBoxRepository.save(ballotBox);

        return ballotBox.getId();
    }

    private List<MenuResponseDto> fetchMenus(CategorySelection selection){
        if (selection.menuIds().contains(0L)){
           return menuModulePort.getMenusByCategoryId(selection.categoryId());
        }
        return menuModulePort.getMenusByIds(selection.menuIds());
    }

    @Transactional
    public Long castVote(Long userId, Long ballotBoxId, List<Long> menus){
        if (menus == null || menus.isEmpty()){
            throw new BusinessException(VoteErrorCode.INVALID_VOTE_COUNT);
        }

        Set<Long> uniqueMenus = new HashSet<>(menus);
        if (uniqueMenus.size() != menus.size()){
            throw new BusinessException(VoteErrorCode.DUPLICATE_MENU_SELECTION);
        }

        if (menus.size() > 3){
            throw new BusinessException(VoteErrorCode.EXCEED_MAX_COUNT);
        }

        if (voteRecordRepository.existsByMemberIdAndBallotBoxId(userId, ballotBoxId)) {
            throw new BusinessException(VoteErrorCode.ALREADY_VOTED);
        }

        BallotBox ballotBox = ballotBoxRepository.findById(ballotBoxId)
                .orElseThrow(() -> new BusinessException(VoteErrorCode.BALLOT_BOX_NOT_FOUND));

        ballotBox.checkAndClose();

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

    @Transactional
    public void deleteBallotBox(Long userId, Long ballotBoxId){
        BallotBox ballotBox = ballotBoxRepository.findById(ballotBoxId)
                .orElseThrow(() -> new BusinessException(VoteErrorCode.BALLOT_BOX_NOT_FOUND));

        ballotBox.delete(userId);
    }

    public List<BallotBoxesResponse> getBallotBoxes(Long memberId, Status status){
        List<Long> myGroupIds = groupModulePort.getMyGroupIds(memberId);

        if (myGroupIds.isEmpty()) {
            return List.of();
        }

        List<BallotBox> ballotBoxes = (status == null)
                ? ballotBoxRepository.findAllByGroupIdInAndStatusNot(myGroupIds, Status.DELETED)
                : ballotBoxRepository.findAllByGroupIdInAndStatus(myGroupIds, status);

        return ballotBoxes.stream()
                .map(BallotBoxesResponse::from)
                .toList();
    }

    @Transactional
    public BallotBoxDetailResponse getBallotBox(Long memberId, Long ballotBoxId){
        BallotBox ballotBox = ballotBoxRepository.findById(ballotBoxId)
                .orElseThrow(() -> new BusinessException(VoteErrorCode.BALLOT_BOX_NOT_FOUND));

        ballotBox.checkAndClose();

        if (!groupModulePort.isMemberOfGroup(memberId, ballotBox.getGroupId())){
            throw new BusinessException(VoteErrorCode.BALLOT_BOX_NOT_FOUND);
        }

        return BallotBoxDetailResponse.from(ballotBox);
    }
}