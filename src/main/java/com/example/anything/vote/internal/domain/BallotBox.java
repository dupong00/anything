package com.example.anything.vote.internal.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ballot_box")
public class BallotBox {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ballotBoxId;

    private Long groupId;
    private String title;
    private LocalDateTime createAt;
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private Status status;

    private double latitude;
    private double longitude;
    private String locationName;

    @OneToMany(mappedBy = "ballotBox", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteOption> voteOptions = new ArrayList<>();

    public static BallotBox create(Long groupId, String title, LocalDateTime deadline,
                                   double latitude, double longitude, String locationName) {
        return BallotBox.builder()
                .groupId(groupId)
                .title(title)
                .createAt(LocalDateTime.now())
                .deadline(deadline)
                .status(Status.ACTIVE)
                .latitude(latitude)
                .longitude(longitude)
                .locationName(locationName)
                .build();
    }

    public void addVoteOption(Long id, String name) {
        VoteOption option = VoteOption.create(id, name, this);
        this.voteOptions.add(option);
    }

    public void checkAndClose() {
        if (this.status == Status.ACTIVE && isExpired()) {
            this.status = Status.CLOSED;
        }
    }

    private boolean isExpired() {
        return LocalDateTime.now().isAfter(this.deadline);
    }
}
