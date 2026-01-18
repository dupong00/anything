package com.example.anything.group.internal.domain;

import com.example.anything.common.BusinessException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`group`")
public class Group {
    // business
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    // domain
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String invitedCode;

    private LocalDateTime expiredAt;

    private Long ownerId;

    // method
    public static Group create(String name, Long ownerId) {
        return Group.builder()
                .invitedCode(generateInvitedCode())
                .name(name)
                .ownerId(ownerId)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    private static String generateInvitedCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public void validateOwner(Long userId) {
        if (this.ownerId == null || !this.ownerId.equals(userId)) {
            throw new BusinessException(GroupErrorCode.NOT_GROUP_OWNER);
        }
    }

    public void validateInviteCode(String invitedCode){
        if (invitedCode == null || !Objects.equals(this.invitedCode, invitedCode)){
            throw new BusinessException(GroupErrorCode.INVALID_INVITE_CODE);
        }

        if (isInviteCodeExpired()){
            throw new BusinessException(GroupErrorCode.EXPIRED_INVITE_CODE);
        }
    }
    private boolean isInviteCodeExpired(){
        return expiredAt != null && expiredAt.isBefore(LocalDateTime.now());
    }
}