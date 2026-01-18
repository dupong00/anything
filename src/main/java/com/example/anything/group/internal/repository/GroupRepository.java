package com.example.anything.group.internal.repository;

import com.example.anything.group.internal.domain.Group;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group,Long> {
    @Query("SELECT g FROM Group g JOIN GroupMember gm ON g.id = gm.group.id " +
            "WHERE gm.memberId = :memberId")
    List<Group> findAllGroupsByMemberId(@Param("memberId") Long memberId);

    Optional<Group> findByInvitedCode(String invitedCode);
}
