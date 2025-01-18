package com.myme.mycarforme.domains.fcm.repository;

import com.myme.mycarforme.domains.fcm.domain.FCMToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
    Optional<FCMToken> findByUserId(String userId);
}
