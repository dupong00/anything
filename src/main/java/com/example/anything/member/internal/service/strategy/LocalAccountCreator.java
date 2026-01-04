package com.example.anything.member.internal.service.strategy;

import com.example.anything.member.dto.MemberSignUpRequest;
import com.example.anything.member.internal.domain.Account;
import com.example.anything.member.internal.domain.LocalAccount;
import com.example.anything.member.internal.domain.Member;
import com.example.anything.member.internal.domain.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalAccountCreator implements AccountCreator {
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(ProviderType providerType) {
        return providerType == ProviderType.LOCAL;
    }

    @Override
    public Account create(Member member, MemberSignUpRequest request){
        return new LocalAccount(
                member,
                request.identifier(),
                request.providerType(),
                passwordEncoder.encode(request.password())
        );
    }
}
