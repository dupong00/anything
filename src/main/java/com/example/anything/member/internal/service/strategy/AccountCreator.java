package com.example.anything.member.internal.service.strategy;


import com.example.anything.member.dto.MemberSignUpRequest;
import com.example.anything.member.internal.domain.Account;
import com.example.anything.member.internal.domain.Member;
import com.example.anything.member.internal.domain.ProviderType;

public interface AccountCreator {
    boolean supports(ProviderType providerType);
    Account create(Member member, MemberSignUpRequest request);
}
