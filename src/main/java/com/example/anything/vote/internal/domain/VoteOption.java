package com.example.anything.vote.internal.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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

    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ballot_box_id")
    private BallotBox ballotBox;

    @Version
    private Long version;

    public static VoteOption create(Long id, String menuName, BallotBox ballotBox) {
        VoteOption voteOption = new VoteOption();

        voteOption.menuId = id;
        voteOption.menuName = menuName;
        voteOption.ballotBox = ballotBox;
        voteOption.count = 0;

        return voteOption;
    }

    public void addCount() {
        this.count = (this.count == null) ? 1 : this.count + 1;
    }
}
