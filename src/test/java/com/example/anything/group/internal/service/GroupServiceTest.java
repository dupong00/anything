package com.example.anything.group.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.example.anything.common.BusinessException;
import com.example.anything.group.internal.domain.Group;
import com.example.anything.group.internal.domain.GroupErrorCode;
import com.example.anything.group.internal.repository.GroupRepository;
import java.util.Optional;
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

    @Test
    @DisplayName("그룹을 성공적으로 생성하고 ID를 반환한다")
    void createGroup() {
        // given
        Group group = Group.create("그룹", 1L);
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
        Long groupId = 1L;
        Long ownerId = 10L;

        Group group = Group.create("테스트 그룹", ownerId);
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
}