package com.example.anything.member.internal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
