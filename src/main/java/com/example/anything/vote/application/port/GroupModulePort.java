package com.example.anything.vote.application.port;

import java.util.List;

public interface GroupModulePort {
    List<Long> getMyGroupIds(Long memberId);

    boolean isMemberOfGroup(Long memberId, Long groupId);
}