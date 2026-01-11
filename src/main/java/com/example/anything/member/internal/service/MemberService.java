package com.example.anything.member.internal.service;

import com.example.anything.common.BusinessException;
import com.example.anything.common.jwt.JwtProvider;
import com.example.anything.common.jwt.JwtToken;
import com.example.anything.member.dto.MemberLoginRequest;
import com.example.anything.member.dto.MemberLoginResponse;
import com.example.anything.member.dto.MemberSignUpRequest;
import com.example.anything.member.internal.domain.Account;
import com.example.anything.member.internal.domain.Member;
import com.example.anything.member.internal.domain.MemberErrorCode;
import com.example.anything.member.internal.domain.MemberProfile;
import com.example.anything.member.internal.domain.MemberRole;
import com.example.anything.member.internal.repository.AccountRepository;
import com.example.anything.member.internal.repository.MemberRepository;
import com.example.anything.member.internal.service.strategy.AccountCreator;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final List<AccountCreator> accountCreator;


    @Transactional
    public void signUp(MemberSignUpRequest request){
        Member member = memberRepository.findByPhoneNumber(request.phoneNumber())
                        .orElseGet(() -> createNewMember(request));

        boolean isAlreadyLinked = member.getAccount().stream()
                .anyMatch(a -> a.getProviderType() == request.providerType());

        if (isAlreadyLinked){
            throw new BusinessException(MemberErrorCode.ALREADY_LINKED_ACCOUNT);
        }

        AccountCreator creator = accountCreator.stream()
                .filter(c -> c.supports(request.providerType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(MemberErrorCode.UNSUPPORTED_PROVIDER));

        Account account = creator.create(member, request);

        member.addAccount(account);

        memberRepository.save(member);
    }

    private Member createNewMember(MemberSignUpRequest request) {
        Member member = new Member();

        member.setMemberProfile(new MemberProfile(
                request.email(),
                request.nickname(),
                request.phoneNumber(),
                member)
        );

        member.setRole(MemberRole.MEMBER);

        return member;
    }

    @Transactional(readOnly = true)
    public MemberLoginResponse login(MemberLoginRequest request){
        // 소셜/로컬 계정이 있는지 조회
        Account account = accountRepository.findWithMember(request.providerType(), request.identifier())
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        account.validatePassword(request.password(), passwordEncoder);

        Member member = account.getMember();

        JwtToken token = jwtProvider.generateToken(member.getId(), String.valueOf(member.getRole()));

        return MemberLoginResponse.create(token);
    }
}
