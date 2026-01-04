package com.example.anything.member.internal.repository;

import com.example.anything.member.internal.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member,Long> {
    @Query("select m from Member m join fetch m.memberProfile p where p.phoneNumber = :phoneNumber")
    Optional<Member> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
