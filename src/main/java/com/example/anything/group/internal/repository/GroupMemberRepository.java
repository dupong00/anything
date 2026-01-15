package com.example.anything.group.internal.repository;

import com.example.anything.group.internal.domain.GroupMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember,Long> {
    @Query("SELECT gm.group.id FROM GroupMember gm WHERE gm.memberId = :memberId")
    List<Long> findAllByMemberId(@Param("memberId") long memberId);

    boolean existsByMemberIdAndGroup_Id(long memberId, long groupId);
}
