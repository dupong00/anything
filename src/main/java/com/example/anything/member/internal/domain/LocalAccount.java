package com.example.anything.member.internal.domain;

import com.example.anything.common.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@DiscriminatorValue("LOCAL")
@NoArgsConstructor
public class LocalAccount extends Account{
    @Column(nullable = false)
    private String password;

    public LocalAccount(Member member, String identifier, ProviderType provider,String password) {
        super(member, identifier, provider);
        this.password = password;
    }

    public void validatePassword(String password, PasswordEncoder encoder) {
        if (!encoder.matches(password, this.password)) {
            throw new BusinessException(MemberErrorCode.INVALID_PASSWORD);
        }
    }
}
