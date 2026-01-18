package com.example.anything.group.internal.repository;

import com.example.anything.group.internal.domain.Group;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends JpaRepository<Group,Long> {
    @Query("SELECT g FROM Group g JOIN GroupMember gm ON g.id = gm.group.id " +
            "WHERE gm.memberId = :memberId AND g.expiredAt > :now")
    List<Group> findActiveGroupsByMemberId(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

    void deleteByExpiredAtBefore(LocalDateTime expiredAt);
}
