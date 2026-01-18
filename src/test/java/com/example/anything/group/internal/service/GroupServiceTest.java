package com.example.anything.group.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.example.anything.common.BusinessException;
import com.example.anything.group.internal.domain.Group;
import com.example.anything.group.internal.domain.GroupErrorCode;
import com.example.anything.group.internal.domain.GroupMember;
import com.example.anything.group.internal.repository.GroupMemberRepository;
import com.example.anything.group.internal.repository.GroupRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    private Group group;
    private final String inviteCode = "ABC123";
    private final Long memberId = 1L;

    @BeforeEach
    void setUp() {
        // 기본 그룹 객체 생성
        group = Group.builder()
                .id(10L)
                .name("테스트 그룹")
                .invitedCode(inviteCode)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .ownerId(1L)
                .build();
    }

    @Test
    @DisplayName("그룹을 성공적으로 생성하고 ID를 반환한다")
    void createGroup() {
        // given
        ReflectionTestUtils.setField(group, "id", 100L);
        given(groupRepository.save(any(Group.class))).willReturn(group);

        // when
        Long savedId = groupService.createGroup("그룹", 1L);

        // then
        assertThat(savedId).isEqualTo(100L);
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    @DisplayName("방장이 그룹 삭제를 요청하면 성공적으로 삭제된다")
    void deleteGroupSuccess() {
        // given
        Long groupId = 10L;
        Long ownerId = 1L;

        ReflectionTestUtils.setField(group, "id", groupId);

        given(groupRepository.findById(groupId)).willReturn(Optional.of(group));

        // when
        groupService.deleteGroup(groupId, ownerId);

        // then
        verify(groupRepository, times(1)).delete(group);
    }

    @Test
    @DisplayName("그룹 삭제 시 존재하지 않는 그룹이면 예외를 던진다")
    void deleteGroupNotFound() {
        // given
        given(groupRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> groupService.deleteGroup(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GroupErrorCode.GROUP_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("참여 중인 활성 그룹 목록을 조회한다")
    void getGroups_Success() {
        // given
        Long memberId = 1L;
        List<Group> expectedGroups = List.of(Group.create("Group A", 1L), Group.create("Group B", 1L));
        given(groupRepository.findAllGroupsByMemberId(eq(memberId)))
                .willReturn(expectedGroups);

        // when
        List<Group> result = groupService.getGroups(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getName()).isEqualTo("Group A");
        verify(groupRepository).findAllGroupsByMemberId(eq(memberId));
    }

    @Test
    @DisplayName("특정 그룹의 멤버 목록을 성공적으로 조회한다")
    void getGroupMembers_Success() {
        // given
        Long memberId = 1L;
        Long groupId = 100L;
        Group group = Group.create("Test Group", 1L);
        List<GroupMember> members = List.of(new GroupMember(), new GroupMember());

        given(groupRepository.findById(groupId)).willReturn(Optional.of(group));
        given(groupMemberRepository.existsByMemberIdAndGroup_Id(memberId, groupId)).willReturn(true);
        given(groupMemberRepository.findAllByGroup_Id(groupId)).willReturn(members);

        // when
        List<GroupMember> result = groupService.getGroupMembers(memberId, groupId);

        // then
        assertThat(result).hasSize(2);
        verify(groupMemberRepository).findAllByGroup_Id(groupId);
    }

    @Test
    @DisplayName("그룹이 존재하지 않으면 멤버 조회 시 예외가 발생한다")
    void getGroupMembers_Fail_NotFound() {
        // given
        given(groupRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> groupService.getGroupMembers(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GroupErrorCode.GROUP_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("그룹 멤버가 아니면 멤버 목록 조회 시 예외가 발생한다")
    void getGroupMembers_Fail_NotMember() {
        // given
        Long groupId = 1L;
        given(groupRepository.findById(groupId)).willReturn(Optional.of(new Group()));
        given(groupMemberRepository.existsByMemberIdAndGroup_Id(anyLong(), eq(groupId))).willReturn(false);

        // when & then
        assertThatThrownBy(() -> groupService.getGroupMembers(1L, groupId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GroupErrorCode.NOT_GROUP_MEMBER.getMessage());
    }

    @Test
    @DisplayName("초대 코드로 그룹 가입에 성공한다")
    void joinGroup_Success() {
        // given
        given(groupRepository.findByInvitedCode(inviteCode)).willReturn(Optional.of(group));
        given(groupMemberRepository.existsByMemberIdAndGroup_Id(memberId, group.getId())).willReturn(false);

        // when
        groupService.joinGroup(memberId, inviteCode);

        // then
        verify(groupMemberRepository, times(1)).save(any(GroupMember.class));
    }

    @Test
    @DisplayName("이미 가입된 그룹인 경우 예외가 발생한다")
    void joinGroup_Fail_AlreadyMember() {
        // given
        given(groupRepository.findByInvitedCode(inviteCode)).willReturn(Optional.of(group));
        given(groupMemberRepository.existsByMemberIdAndGroup_Id(memberId, group.getId())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> groupService.joinGroup(memberId, inviteCode))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GroupErrorCode.ALREADY_GROUP_MEMBER.getMessage());
    }

    @Test
    @DisplayName("초대 코드가 만료된 경우 가입에 실패한다")
    void joinGroup_Fail_ExpiredCode() {
        // given: 만료 시간을 과거로 설정
        ReflectionTestUtils.setField(group, "expiredAt", LocalDateTime.now().minusHours(1));
        given(groupRepository.findByInvitedCode(inviteCode)).willReturn(Optional.of(group));

        // when & then
        assertThatThrownBy(() -> groupService.joinGroup(memberId, inviteCode))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GroupErrorCode.EXPIRED_INVITE_CODE.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 초대 코드인 경우 가입에 실패한다")
    void joinGroup_Fail_NotFound() {
        // given
        given(groupRepository.findByInvitedCode(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> groupService.joinGroup(memberId, "WRONG"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GroupErrorCode.GROUP_NOT_FOUND.getMessage());
    }
}