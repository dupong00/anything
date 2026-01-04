package com.example.anything.member.internal.service.strategy;

import com.example.anything.member.dto.MemberSignUpRequest;
import com.example.anything.member.internal.domain.Account;
import com.example.anything.member.internal.domain.Member;
import com.example.anything.member.internal.domain.ProviderType;
import com.example.anything.member.internal.domain.SocialAccount;
import org.springframework.stereotype.Component;

@Component
public class SocialAccountCreator implements AccountCreator{
    @Override
    public boolean supports(ProviderType providerType){
        return providerType != ProviderType.LOCAL;
    }

    @Override
    public Account create(Member member, MemberSignUpRequest request){
        return new SocialAccount(
                member,
                request.identifier(),
                request.providerType()
        );
    }
}
