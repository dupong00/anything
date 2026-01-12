package com.example.anything.vote.internal.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vote_record")
public class VoteRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ballot_box_id")
    private BallotBox ballotBox;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "vote_option_id")
    private VoteOption voteOption;

    public static VoteRecord create(Long memberId, BallotBox ballotBox, VoteOption voteOption) {
        voteOption.addCount();

        return VoteRecord.builder()
                .memberId(memberId)
                .ballotBox(ballotBox)
                .voteOption(voteOption)
                .build();
    }
}
