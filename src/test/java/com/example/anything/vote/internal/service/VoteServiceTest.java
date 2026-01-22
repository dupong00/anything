package com.example.anything.vote.internal.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

import com.example.anything.common.BusinessException;
import com.example.anything.menu.application.port.MenuResponseDto;
import com.example.anything.vote.Status;
import com.example.anything.group.application.port.GroupModulePort;
import com.example.anything.menu.application.port.MenuModulePort;
import com.example.anything.vote.dto.*;
import com.example.anything.vote.internal.domain.*;
import com.example.anything.vote.internal.repository.*;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        CategorySelection categorySelection = new CategorySelection(1L, List.of(1L, 2L, 3L));
        BallotBoxRequest request = new BallotBoxRequest(groupId, 37.0, 127.0, "동두천 집", "오늘 점심 메뉴", List.of(categorySelection), LocalDateTime.now());
        given(menuModulePort.getMenusByIds(anyList())).willReturn(List.of(
                new MenuResponseDto(1L, "김치찌개"),
                new MenuResponseDto(2L, "된장찌개")
        ));

        voteService.createBallotBox(request, memberId);

        verify(ballotBoxRepository, times(1)).save(any(BallotBox.class));
    }

    @Test
    @DisplayName("투표하기: 정상적인 투표를 할 경우 성공한다.")
    void castVote_Success() {
        List<Long> menus = List.of(1L, 2L);

        BallotBox ballotBox = BallotBox.create(groupId, memberId,"오늘 점심 메뉴", LocalDateTime.now().plusDays(1), 0, 0, "장소");

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
        BallotBox box1 = BallotBox.create(100L, memberId, "투표 1", LocalDateTime.now().plusDays(1), 0.0, 0.0, "장소 1");
        BallotBox box2 = BallotBox.create(200L, memberId, "투표 2", LocalDateTime.now().plusDays(1), 0.0, 0.0, "장소 2");
        given(ballotBoxRepository.findAllByGroupIdInAndStatusNot(myGroupIds, Status.DELETED)).willReturn(List.of(box1, box2));

        // when
        List<BallotBoxesResponse> result = voteService.getBallotBoxes(memberId, null);

        // then
        assertThat(result).hasSize(2); // 2개가 반환되었는지 확인
        assertThat(result.get(0).title()).isEqualTo("투표 1");
        assertThat(result.get(1).title()).isEqualTo("투표 2");

        verify(ballotBoxRepository).findAllByGroupIdInAndStatusNot(myGroupIds, Status.DELETED);
    }

    @Test
    @DisplayName("목록 조회: 사용자가 속한 그룹이 없으면 즉시 빈 리스트를 반환한다")
    void getBallotBoxes_EmptyGroup_EarlyReturn() {
        given(groupModulePort.getMyGroupIds(memberId)).willReturn(List.of());

        // when
        List<BallotBoxesResponse> result = voteService.getBallotBoxes(memberId, null);

        // then
        assertThat(result).isEmpty();
        verify(ballotBoxRepository, never()).findAllByGroupIdInAndStatusNot(any(), any());
    }

    @Test
    @DisplayName("상세 조회: 그룹 멤버가 존재하는 투표함을 조회하면 상세 정보를 반환한다")
    void getBallotBox_Success() {
        // given
        BallotBox ballotBox = BallotBox.create(groupId, memberId, "점심 메뉴 투표", LocalDateTime.now().plusDays(1), 37.0, 127.0, "식당");
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
        BallotBox ballotBox = BallotBox.create(groupId, memberId, "제목", LocalDateTime.now().plusDays(1), 0.0, 0.0, "장소");
        given(ballotBoxRepository.findById(ballotBoxId)).willReturn(Optional.of(ballotBox));
        given(groupModulePort.isMemberOfGroup(memberId, groupId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> voteService.getBallotBox(memberId, ballotBoxId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", VoteErrorCode.BALLOT_BOX_NOT_FOUND);
    }

    @Test
    @DisplayName("투표함 생성자가 삭제를 요청하면 상태를 DELETED로 변경한다")
    void deleteBallotBox_Success() {
        // given
        BallotBox ballotBox = BallotBox.builder()
                .id(ballotBoxId)
                .creatorId(memberId)
                .status(Status.ACTIVE)
                .build();

        given(ballotBoxRepository.findById(ballotBoxId)).willReturn(Optional.of(ballotBox));

        // when
        voteService.deleteBallotBox(memberId, ballotBoxId);

        // then
        assertEquals(Status.DELETED, ballotBox.getStatus());
        assertNotNull(ballotBox.getClosedAt());
    }

    @Test
    @DisplayName("존재하지 않는 투표함 ID인 경우 예외가 발생한다")
    void deleteBallotBox_NotFound() {
        // given
        given(ballotBoxRepository.findById(ballotBoxId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> voteService.deleteBallotBox(memberId, ballotBoxId));

        assertEquals(VoteErrorCode.BALLOT_BOX_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 생성자가 아닌 유저가 삭제를 요청하면 권한 예외가 발생한다")
    void deleteBallotBox_NoAuthority() {
        // given
        Long otherMemberId = 999L;
        BallotBox ballotBox = BallotBox.builder()
                .id(ballotBoxId)
                .creatorId(memberId) // 실제 생성자는 1L
                .status(Status.ACTIVE)
                .build();

        given(ballotBoxRepository.findById(ballotBoxId)).willReturn(Optional.of(ballotBox));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> voteService.deleteBallotBox(otherMemberId, ballotBoxId));

        assertEquals(VoteErrorCode.BALLOT_BOX_NOT_AUTHORITY, exception.getErrorCode());
        // 상태가 변경되지 않았는지 확인
        assertEquals(Status.ACTIVE, ballotBox.getStatus());
    }

    @Test
    @DisplayName("마감 시간이 지나고 동표일 때 우승자는 랜덤하게 3명 선정")
    void calculateWinner_WithTenTie_ShouldSelectThree() {
        // given
        BallotBox ballotBox = BallotBox.builder()
                .status(Status.ACTIVE)
                .deadline(LocalDateTime.now().minusMinutes(1))
                .voteOptions(new ArrayList<>())
                .winnerMenuIds(new ArrayList<>())
                .build();

        for (long i = 1; i <= 10; i++) {
            VoteOption option = VoteOption.create(i, "메뉴" + i, ballotBox);
            ballotBox.getVoteOptions().add(option);
        }

        for (long i = 1; i <= 10; i++) {
            VoteOption option = VoteOption.create(i, "메뉴" + i, ballotBox);
            option.addCount();
            ballotBox.getVoteOptions().add(option);
        }

        ballotBox.checkAndClose();

        assertThat(ballotBox.getStatus()).isEqualTo(Status.CLOSED);
        assertThat(ballotBox.getWinnerMenuIds()).hasSize(3);
    }

    @Test
    @DisplayName("마감 시간이 지나지 않았다면 status는 ACTIVE를 유지하고 우승자는 선정 안됨")
    void checkAndClose_BeforeDeadline_ShouldNotChangeStatus() {
        BallotBox ballotBox = BallotBox.builder()
                .status(Status.ACTIVE)
                .deadline(LocalDateTime.now().plusHours(1))
                .winnerMenuIds(new ArrayList<>())
                .build();

        ballotBox.checkAndClose();

        assertThat(ballotBox.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(ballotBox.getWinnerMenuIds()).isEmpty();
    }

    @Test
    @DisplayName("메뉴 ID가 0L인 경우 해당 카테고리의 모든 메뉴를 가져온다")
    void createBallotBox_withCategoryAll() {
        // given
        Long memberId = 1L;
        // 10번 카테고리 전체(0L) 선택
        CategorySelection selection = new CategorySelection(10L, List.of(0L));
        BallotBoxRequest request = createRequest(List.of(selection));

        List<MenuResponseDto> categoryMenus = List.of(
                new MenuResponseDto(1L, "김치찌개"),
                new MenuResponseDto(2L, "된장찌개")
        );

        given(menuModulePort.getMenusByCategoryId(10L)).willReturn(categoryMenus);

        // when
        voteService.createBallotBox(request, memberId);

        // then
        verify(menuModulePort).getMenusByCategoryId(10L); // 전체 조회 메서드 호출 확인
        verify(menuModulePort, never()).getMenusByIds(any()); // 개별 ID 조회는 호출되지 않아야 함
        verify(ballotBoxRepository).save(any(BallotBox.class)); // 최종 투표함 저장 확인
    }

    @Test
    @DisplayName("여러 카테고리에서 중복된 메뉴가 들어와도 Set을 통해 중복 제거된다")
    void createBallotBox_deduplicationTest() {
        Long memberId = 1L;
        CategorySelection sel1 = new CategorySelection(10L, List.of(1L, 2L));
        CategorySelection sel2 = new CategorySelection(20L, List.of(1L));
        BallotBoxRequest request = createRequest(List.of(sel1, sel2));

        given(menuModulePort.getMenusByIds(List.of(1L, 2L)))
                .willReturn(List.of(new MenuResponseDto(1L, "메뉴1"), new MenuResponseDto(2L, "메뉴2")));
        given(menuModulePort.getMenusByIds(List.of(1L)))
                .willReturn(List.of(new MenuResponseDto(1L, "메뉴1")));

        // when
        voteService.createBallotBox(request, memberId);

        // then
        ArgumentCaptor<BallotBox> captor = ArgumentCaptor.forClass(BallotBox.class);
        verify(ballotBoxRepository).save(captor.capture());
    }

    private BallotBoxRequest createRequest(List<CategorySelection> selections) {
        return new BallotBoxRequest(
                1L,             // groupId
                37.0,           // latitude
                127.0,          // longitude
                "강남역",        // locationName
                "오늘 뭐 먹지?",  // title
                selections,     // selections
                LocalDateTime.now().plusHours(1) // deadline
        );
    }
}