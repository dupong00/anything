package com.example.anything.member.internal.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_profile")
public class MemberProfile {
    @Id
    private Long id;

    private String email;

    private String nickname;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    public  MemberProfile(String email, String nickname, String phoneNumber, Member member) {
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.member = member;
    }
}