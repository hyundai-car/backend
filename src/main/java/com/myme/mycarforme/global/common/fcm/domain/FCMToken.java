package com.myme.mycarforme.global.common.fcm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="fcm_tokens")
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String userId;

    private String deviceToken;
    private LocalDateTime updatedAt;

    @Builder
    private FCMToken(Long id, String userId, String deviceToken, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.updatedAt = updatedAt;
    }

    public void updateToken(String newToken) {
        this.deviceToken = newToken;
    }
}
