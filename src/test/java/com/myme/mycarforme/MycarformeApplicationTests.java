package com.myme.mycarforme;

import com.myme.mycarforme.domains.auth.client.KeycloakClient;
import com.myme.mycarforme.global.common.fcm.service.FCMTokenService;
import com.myme.mycarforme.global.common.sms.service.SmsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
class MycarformeApplicationTests {

	@MockitoBean
	private KeycloakClient keycloakClient;

	@MockitoBean
	private JwtDecoder jwtDecoder;

	@MockitoBean
	private ClientRegistrationRepository clientRegistrationRepository;

	// OAuth2 관련 추가 Mock 빈들
	@MockitoBean
	private OAuth2AuthorizedClientService authorizedClientService;

	@MockitoBean
	private OAuth2AuthorizedClientRepository authorizedClientRepository;

	@MockitoBean
	private OAuth2AuthorizedClient authorizedClient;

	@MockitoBean
	private FCMTokenService fcmTokenService;

	@MockitoBean
	private SmsService smsService;

	@Test
	void contextLoads() {
	}
}
