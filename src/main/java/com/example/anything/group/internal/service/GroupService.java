package com.example.anything.group.internal.service;

import com.example.anything.common.BusinessException;
import com.example.anything.group.internal.domain.Group;
import com.example.anything.group.internal.domain.GroupErrorCode;
import com.example.anything.group.internal.domain.GroupMember;
import com.example.anything.group.internal.repository.GroupMemberRepository;
import com.example.anything.group.internal.repository.GroupRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public Long createGroup(String name, Long ownerId){
        Group group = Group.create(name, ownerId);
        return groupRepository.save(group).getId();
    }

    @Transactional
    public void deleteGroup(Long groupId, Long ownerId){
        Group group = groupRepository.findById(groupId)
                        .orElseThrow(() -> new BusinessException(GroupErrorCode.GROUP_NOT_FOUND));

        group.validateOwner(ownerId);

        groupMemberRepository.deleteAllByGroupId(groupId);

        groupRepository.delete(group);
    }

    public List<Group> getGroups(Long memberId){
        return groupRepository.findAllGroupsByMemberId(memberId);
    }

    public List<GroupMember> getGroupMembers(Long memberId,Long groupId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(GroupErrorCode.GROUP_NOT_FOUND));

        if (!groupMemberRepository.existsByMemberIdAndGroup_Id(memberId, groupId)){
            throw new BusinessException(GroupErrorCode.NOT_GROUP_MEMBER);
        }

        return groupMemberRepository.findAllByGroup_Id(groupId);
    }

    @Transactional
    public void joinGroup(Long memberId, String inviteCode){
        Group group = groupRepository.findByInvitedCode(inviteCode)
                .orElseThrow(() -> new BusinessException(GroupErrorCode.GROUP_NOT_FOUND));

        if (groupMemberRepository.existsByMemberIdAndGroup_Id(memberId, group.getId())){
            throw new BusinessException(GroupErrorCode.ALREADY_GROUP_MEMBER);
        }

        group.validateInviteCode(inviteCode);

        GroupMember newMember = GroupMember.create(memberId, group);

        groupMemberRepository.save(newMember);
    }
}