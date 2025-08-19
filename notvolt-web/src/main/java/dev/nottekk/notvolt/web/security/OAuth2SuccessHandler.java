package dev.nottekk.notvolt.web;

import dev.nottekk.notvolt.persistence.entity.UserTokenEntity;
import dev.nottekk.notvolt.persistence.repo.UserTokenRepository;
import dev.nottekk.notvolt.web.security.CryptoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private final UserTokenRepository userTokenRepository;
	private final CryptoService cryptoService;

	public OAuth2SuccessHandler(UserTokenRepository userTokenRepository, CryptoService cryptoService) {
		this.userTokenRepository = userTokenRepository;
		this.cryptoService = cryptoService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		if (authentication instanceof OAuth2AuthenticationToken oauth) {
			OAuth2User principal = oauth.getPrincipal();
			String id = principal.getAttribute("id");
			String accessToken = (String) request.getSession().getAttribute("SPRING_SECURITY_OAUTH2_ACCESS_TOKEN");
			String refreshToken = (String) request.getSession().getAttribute("SPRING_SECURITY_OAUTH2_REFRESH_TOKEN");
			Instant expiresAtInstant = (Instant) request.getSession().getAttribute("SPRING_SECURITY_OAUTH2_EXPIRES_AT");
			OffsetDateTime expiresAt = expiresAtInstant != null ? expiresAtInstant.atOffset(ZoneOffset.UTC) : null;
			if (id != null && accessToken != null) {
				UserTokenEntity e = userTokenRepository.findById(id).orElseGet(UserTokenEntity::new);
				e.setDiscordId(id);
				e.setEncAccessToken(cryptoService.encrypt(accessToken));
				e.setEncRefreshToken(cryptoService.encrypt(refreshToken));
				e.setExpiresAt(expiresAt);
				userTokenRepository.save(e);
			}
		}
		response.sendRedirect("/");
	}
}
