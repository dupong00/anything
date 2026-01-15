package com.example.anything.group.infrastructure.adapter;

import com.example.anything.group.internal.domain.GroupMember;
import com.example.anything.group.internal.repository.GroupMemberRepository;
import com.example.anything.vote.application.port.GroupModulePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupModuleAdapter implements GroupModulePort {
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public List<Long> getMyGroupIds(Long memberId){
        return groupMemberRepository.findAllByMemberId(memberId);
    }

    @Override
    public boolean isMemberOfGroup(Long memberId, Long groupId) {
        return groupMemberRepository.existsByMemberIdAndGroupId(memberId,groupId);
    }
}
