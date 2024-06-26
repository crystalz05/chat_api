package com.chat.app.chatApi.jwt.security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
	
	private JwtEncoder jwtEncoder;

	
	public JwtTokenService(JwtEncoder jwtEncoder) {
		super();
		this.jwtEncoder = jwtEncoder;
	}

	public String createToken(Authentication authentication) {
		var claims = JwtClaimsSet.builder()
		.issuer("self")
		.issuedAt(Instant.now())
		.expiresAt(Instant.now().plusSeconds(60 *30))
		.subject(authentication.getName())
		.claim("scope", createScope(authentication))
		.build();
		
		JwtEncoderParameters parameters = JwtEncoderParameters.from(claims);
		return jwtEncoder.encode(parameters)
				.getTokenValue();
	}

	public  String createScope(Authentication authentication) {
		return authentication.getAuthorities()
		.stream().map(a-> a.getAuthority())
		.collect(Collectors.joining(" "));
	}

}
