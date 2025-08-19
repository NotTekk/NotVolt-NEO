package dev.nottekk.notvolt.web.security;

import dev.nottekk.notvolt.persistence.entity.ApiTokenEntity;
import dev.nottekk.notvolt.persistence.repo.ApiTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Component
public class ApiAuthFilter extends OncePerRequestFilter {
	private final ApiTokenRepository tokens;

	public ApiAuthFilter(ApiTokenRepository tokens) {
		this.tokens = tokens;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String path = request.getRequestURI();
		if (!path.startsWith("/api/")) { filterChain.doFilter(request, response); return; }
		String key = request.getHeader("X-Api-Key");
		if (key == null || key.isBlank()) { response.setStatus(HttpStatus.UNAUTHORIZED.value()); return; }
		String hash = DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
		Optional<ApiTokenEntity> tok = tokens.findByHash(hash);
		if (tok.isEmpty()) { response.setStatus(HttpStatus.UNAUTHORIZED.value()); return; }
		ApiTokenEntity t = tok.get();
		String allow = t.getAllowedIps();
		if (allow != null && !allow.isBlank()) {
			String ip = request.getRemoteAddr();
			boolean ok = Arrays.stream(allow.split(",")).map(String::trim).anyMatch(ip::equals);
			if (!ok) { response.setStatus(HttpStatus.UNAUTHORIZED.value()); return; }
		}
		request.setAttribute("apiScopes", t.getScopes());
		request.setAttribute("apiGuildId", t.getGuildId());
		filterChain.doFilter(request, response);
	}
}
