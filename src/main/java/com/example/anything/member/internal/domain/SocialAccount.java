package com.example.anything.member.internal.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@DiscriminatorValue("SOCIAL")
@Getter
@NoArgsConstructor
public class SocialAccount extends Account {
    public SocialAccount(Member member, String identifier, ProviderType provider) {
        super(member, identifier, provider);
    }

    @Override
    public void validatePassword(String password, PasswordEncoder encoder) { }
}
