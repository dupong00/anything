package com.example.anything.vote.internal.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vote_option")
public class VoteOption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long menuId;

    private String menuName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ballot_box_id")
    private BallotBox ballotBox;

    public static VoteOption create(Long id, String menuName, BallotBox ballotBox) {
        VoteOption voteOption = new VoteOption();

        voteOption.menuId = id;
        voteOption.menuName = menuName;
        voteOption.ballotBox = ballotBox;

        return voteOption;
    }
}
