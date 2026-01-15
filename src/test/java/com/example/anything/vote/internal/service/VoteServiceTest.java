package com.example.anything.vote.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.example.anything.common.BusinessException;
import com.example.anything.vote.application.port.GroupModulePort;
import com.example.anything.vote.application.port.MenuModulePort;
import com.example.anything.vote.dto.*;
import com.example.anything.vote.internal.domain.*;
import com.example.anything.vote.internal.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock private MenuModulePort menuModulePort;
    @Mock private GroupModulePort groupModulePort;
    @Mock private BallotBoxRepository ballotBoxRepository;
    @Mock private VoteOptionRepository voteOptionRepository;
    @Mock private VoteRecordRepository voteRecordRepository;

    @InjectMocks
    private VoteService voteService;

    private final Long memberId = 1L;
    private final Long ballotBoxId = 10L;
    private final Long groupId = 100L;

    @Test
    @DisplayName("투표함 생성: 정상적인 메뉴 목록으로 투표함을 생성하면 성공한다")
    void createBallotBox_Success() {
        BallotBoxRequest request = new BallotBoxRequest(groupId, 37.0, 127.0, "동두천 집", "오늘 점심 메뉴", List.of(1L, 2L), LocalDateTime.now());
        given(menuModulePort.getMenusByIds(anyList())).willReturn(List.of(
                new MenuResponseDto(1L, "김치찌개"),
                new MenuResponseDto(2L, "된장찌개")
        ));

        voteService.createBallotBox(request);

        verify(ballotBoxRepository, times(1)).save(any(BallotBox.class));
    }

    @Test
    @DisplayName("투표하기: 정상적인 투표를 할 경우 성공한다.")
    void castVote_Success() {
        List<Long> menus = List.of(1L, 2L);

        BallotBox ballotBox = BallotBox.create(groupId, "점심 메뉴 투표", LocalDateTime.now().plusDays(1), 0, 0, "장소");

        given(voteRecordRepository.existsByMemberIdAndBallotBoxId(memberId, ballotBoxId)).willReturn(false);
        given(ballotBoxRepository.findById(ballotBoxId)).willReturn(Optional.of(ballotBox));

        VoteOption option1 = mock(VoteOption.class);
        VoteOption option2 = mock(VoteOption.class);
        given(voteOptionRepository.findByBallotBox_IdAndMenuId(ballotBoxId, 1L)).willReturn(Optional.of(option1));
        given(voteOptionRepository.findByBallotBox_IdAndMenuId(ballotBoxId, 2L)).willReturn(Optional.of(option2));

        Long resultId = voteService.castVote(memberId, ballotBoxId, menus);

        assertThat(resultId).isEqualTo(ballotBoxId);

        verify(option1, times(1)).addCount();
        verify(option2, times(1)).addCount();

        verify(voteRecordRepository, times(2)).save(any(VoteRecord.class));

    }

    @Test
    @DisplayName("투표하기: 이미 투표한 유저가 다시 투표를 시도하면 예외가 발생한다")
    void castVote_Fail_AlreadyVoted() {
        given(voteRecordRepository.existsByMemberIdAndBallotBoxId(memberId, ballotBoxId)).willReturn(true);

        assertThatThrownBy(() -> voteService.castVote(memberId, ballotBoxId, List.of(1L)))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", VoteErrorCode.ALREADY_VOTED);
    }

    @Test
    @DisplayName("목록 조회: 사용자가 속한 그룹의 투표함 목록을 정상적으로 반환한다")
    void getBallotBoxes_Success() {
        // given
        List<Long> myGroupIds = List.of(100L, 200L);
        given(groupModulePort.getMyGroupIds(memberId)).willReturn(myGroupIds);

        // 가짜 투표함 리스트 생성
        BallotBox box1 = BallotBox.create(100L, "투표 1", null, 0.0, 0.0, "장소 1");
        BallotBox box2 = BallotBox.create(200L, "투표 2", null, 0.0, 0.0, "장소 2");
        given(ballotBoxRepository.findAllByGroupIdIn(myGroupIds)).willReturn(List.of(box1, box2));

        // when
        List<BallotBoxesResponse> result = voteService.getBallotBoxes(memberId, null);

        // then
        assertThat(result).hasSize(2); // 2개가 반환되었는지 확인
        assertThat(result.get(0).title()).isEqualTo("투표 1");
        assertThat(result.get(1).title()).isEqualTo("투표 2");

        verify(ballotBoxRepository).findAllByGroupIdIn(myGroupIds);
    }

    @Test
    @DisplayName("목록 조회: 사용자가 속한 그룹이 없으면 즉시 빈 리스트를 반환한다")
    void getBallotBoxes_EmptyGroup_EarlyReturn() {
        given(groupModulePort.getMyGroupIds(memberId)).willReturn(List.of());

        // when
        List<BallotBoxesResponse> result = voteService.getBallotBoxes(memberId, null);

        // then
        assertThat(result).isEmpty();
        verify(ballotBoxRepository, never()).findAllByGroupIdIn(any());
    }

    @Test
    @DisplayName("상세 조회: 그룹 멤버가 존재하는 투표함을 조회하면 상세 정보를 반환한다")
    void getBallotBox_Success() {
        // given
        BallotBox ballotBox = BallotBox.create(groupId, "점심 메뉴 투표", LocalDateTime.now().plusDays(1), 37.0, 127.0, "식당");
        ballotBox.addVoteOption(1L, "김치찌개");

        given(ballotBoxRepository.findById(ballotBoxId)).willReturn(Optional.of(ballotBox));
        given(groupModulePort.isMemberOfGroup(memberId, groupId)).willReturn(true);

        // when
        BallotBoxDetailResponse result = voteService.getBallotBox(memberId, ballotBoxId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("점심 메뉴 투표");
        assertThat(result.locationName()).isEqualTo("식당");
        assertThat(result.options()).hasSize(1);
        assertThat(result.options().getFirst().menuName()).isEqualTo("김치찌개");

        // 권한 체크가 정상적으로 수행되었는지 확인
        verify(groupModulePort).isMemberOfGroup(memberId, groupId);
    }

    @Test
    @DisplayName("상세 조회: 그룹 멤버가 아닌 유저가 조회하면 BALLOT_BOX_NOT_FOUND 예외를 던진다")
    void getBallotBox_Forbidden_Member() {
        // given
        BallotBox ballotBox = BallotBox.create(groupId, "제목", null, 0.0, 0.0, "장소");
        given(ballotBoxRepository.findById(ballotBoxId)).willReturn(Optional.of(ballotBox));
        given(groupModulePort.isMemberOfGroup(memberId, groupId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> voteService.getBallotBox(memberId, ballotBoxId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", VoteErrorCode.BALLOT_BOX_NOT_FOUND);
    }
}