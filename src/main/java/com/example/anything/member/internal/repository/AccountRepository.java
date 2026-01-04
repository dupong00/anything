package com.example.anything.member.internal.repository;

import com.example.anything.member.internal.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
