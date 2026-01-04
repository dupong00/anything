package com.example.anything.member.internal.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SOCIAL")
@Getter
@NoArgsConstructor
public class SocialAccount extends Account {
    public SocialAccount(Member member, String identifier, ProviderType provider) {
        super(member, identifier, provider);
    }
}
