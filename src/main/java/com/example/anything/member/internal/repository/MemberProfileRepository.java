package com.example.anything.member.internal.repository;

import com.example.anything.member.internal.domain.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

}
