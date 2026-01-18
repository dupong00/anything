package com.example.anything.group.internal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "group_member",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_group_member_member_group",
                columnNames = {"member_id", "group_id"} // DB 컬럼명 기준
        )
)
public class GroupMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "member_id")
    Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    Group group;

    public static GroupMember create(Long memberId, Group group){
        return GroupMember.builder()
                .memberId(memberId)
                .group(group)
                .build();
    }
}
