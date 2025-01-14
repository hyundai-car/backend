package com.myme.mycarforme.domains.auth.api;

import com.myme.mycarforme.domains.auth.api.request.LoginRequest;
import com.myme.mycarforme.domains.auth.api.request.ReissueRequest;
import com.myme.mycarforme.domains.auth.api.response.LoginResponse;
import com.myme.mycarforme.domains.auth.api.response.ReissueResponse;
import com.myme.mycarforme.domains.auth.dto.KeycloakTokenDto;
import com.myme.mycarforme.domains.auth.service.AuthService;
import com.myme.mycarforme.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public CommonResponse<LoginResponse> login(
            @RequestBody LoginRequest loginRequest) {
        return CommonResponse.from(authService.login(loginRequest.authorizationCode(), loginRequest.codeVerifier()));
    }

    @PostMapping("/reissue")
    public CommonResponse<ReissueResponse> refreshToken(
            @RequestBody ReissueRequest reissueRequest) {
        return CommonResponse.from(authService.reissue(reissueRequest.refreshToken()));
    }

    @PostMapping("/logout")
    public void logout(
            @RequestParam("refresh_token") String refreshToken) {
        authService.logout(refreshToken);
    }
}
