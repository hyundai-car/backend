package com.myme.mycarforme.global.common.fcm.repository;

import com.myme.mycarforme.global.common.fcm.domain.FCMToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
    Optional<FCMToken> findByUserId(String userId);
}
