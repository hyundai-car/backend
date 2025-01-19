package com.myme.mycarforme.global.common.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.myme.mycarforme.global.common.fcm.domain.FCMToken;
import com.myme.mycarforme.global.common.fcm.exception.FCMTokenNotFoundException;
import com.myme.mycarforme.global.common.fcm.repository.FCMTokenRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMTokenService {
    private final FCMTokenRepository fcmTokenRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    public void updateFCMToken(String userId, String token) {
        FCMToken fcmToken = fcmTokenRepository.findByUserId(userId)
                .orElseGet(() -> FCMToken.builder()
                        .userId(userId)
                        .updatedAt(LocalDateTime.now())
                        .build());

        fcmToken.updateToken(token);
        fcmTokenRepository.save(fcmToken);
    }

    public void sendNotification(String userId, String title, String body) {
        FCMToken token = fcmTokenRepository.findByUserId(userId)
                .orElseThrow(FCMTokenNotFoundException::new);

        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(token.getDeviceToken())
                    .build();

            String response = firebaseMessaging.send(message);
            log.info("Successfully sent message: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send message to token: {}", token.getDeviceToken());
        }
    }
}
