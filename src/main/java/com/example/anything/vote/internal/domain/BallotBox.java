package com.example.anything.vote.internal.domain;

import com.example.anything.common.BusinessException;
import com.example.anything.vote.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ballot_box")
public class BallotBox {
    private static final int WINNERS_MAX_COUNT = 3;

    // domain
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ballot_box_id")
    private Long id;

    private Long groupId;
    private Long creatorId; // 생성자
    private String title;
    private LocalDateTime createAt;
    private LocalDateTime closedAt;
    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private Status status;

    private double latitude;
    private double longitude;
    private String locationName;

    @ElementCollection
    @Builder.Default
    private List<Long> winnerMenuIds = new ArrayList<>();

    @OneToMany(mappedBy = "ballotBox", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteOption> voteOptions = new ArrayList<>();

    // method
    public static BallotBox create(Long groupId, Long creatorId, String title, LocalDateTime deadline,
                                   double latitude, double longitude, String locationName) {

        if (deadline == null) {
            throw new BusinessException(VoteErrorCode.INVALID_DEADLINE);
        }

        return BallotBox.builder()
                .groupId(groupId)
                .creatorId(creatorId)
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
        if (this.status != Status.ACTIVE || !this.deadline.isBefore(LocalDateTime.now())) {
            return;
        }

        this.status = Status.CLOSED;
        this.closedAt = LocalDateTime.now();

        this.calculateWinner();
    }

    private void calculateWinner(){
        int maxCount = this.getVoteOptions().stream()
                .mapToInt(opt -> opt.getCount() != null ? opt.getCount() : 0)
                .max()
                .orElse(0);

        if (maxCount == 0) return;

        List<Long> candidates = this.getVoteOptions().stream()
                .filter(opt -> opt.getCount() != null &&  opt.getCount() == maxCount)
                .map(VoteOption::getMenuId)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(candidates);

        this.winnerMenuIds = candidates.stream()
                .limit(WINNERS_MAX_COUNT)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void delete(Long memberId){
        if (!Objects.equals(memberId, this.creatorId)){
            throw new BusinessException(VoteErrorCode.BALLOT_BOX_NOT_AUTHORITY);
        }

        if (this.status == Status.DELETED) {
            throw new BusinessException(VoteErrorCode.ALREADY_DELETED);
        }

        this.status = Status.DELETED;
        this.closedAt = LocalDateTime.now();
    }
}
