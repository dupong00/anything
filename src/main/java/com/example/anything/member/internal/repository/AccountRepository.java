package com.example.anything.member.internal.repository;

import com.example.anything.member.internal.domain.Account;
import com.example.anything.member.internal.domain.ProviderType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT a FROM Account a JOIN FETCH a.member WHERE a.providerType = :providerType AND a.identifier = :identifier")
    Optional<Account> findWithMember(@Param("providerType") ProviderType providerType, @Param("identifier") String identifier);
}
