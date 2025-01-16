package com.myme.mycarforme.domains.test.api;

import com.myme.mycarforme.global.common.response.CommonResponse;
import com.myme.mycarforme.global.util.security.SecurityUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/get")
    public CommonResponse<Void> getMethod() {

        String name = SecurityUtil.getUserId();

        // Role을 String으로 가져오기
        List<String> role = SecurityUtil.getUserRoleList()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // JWT를 String으로 가져오기
        String jwtString = Optional.ofNullable(SecurityUtil.getCurrentJwt())
                .map(Jwt::getTokenValue)
                .orElse("");

        // 테스트를 위해 출력
        System.out.println("Name: " + name);
        System.out.println("Role: " + role);
        System.out.println("JWT: " + jwtString);

        return CommonResponse.empty();

    }

    @PostMapping("/post")
    public CommonResponse<Dto> postMethod(
            @RequestParam int id,
            @RequestParam String name
    ) {
        return CommonResponse.from(new Dto(id, name));
    }

    public record Dto(
            int id,
            String carName
    ) {
    }
}