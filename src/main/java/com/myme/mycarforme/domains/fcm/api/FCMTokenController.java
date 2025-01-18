package com.myme.mycarforme.domains.fcm.api;

import com.myme.mycarforme.domains.fcm.api.request.FCMTokenRequest;
import com.myme.mycarforme.domains.fcm.service.FCMTokenService;
import com.myme.mycarforme.global.common.response.CommonResponse;
import com.myme.mycarforme.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FCMTokenController {
    private final FCMTokenService fcmTokenService;

    @PostMapping("/token")
    public CommonResponse<String> registerToken(
            @RequestBody FCMTokenRequest token
    ) {
        String userId = SecurityUtil.getUserId();
        fcmTokenService.updateFCMToken(userId, token.token());
        return CommonResponse.from("Token registered successfully");
    }
}
