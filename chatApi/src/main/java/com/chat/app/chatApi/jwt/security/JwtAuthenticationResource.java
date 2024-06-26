package com.chat.app.chatApi.jwt.security;

import java.time.Instant;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.oldone.chat.app.chatApi.jwt.JwtAuthenticationController;
import com.oldone.chat.app.chatApi.jwt.JwtTokenRequest;
import com.oldone.chat.app.chatApi.jwt.JwtTokenResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class JwtAuthenticationResource {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);

    private AuthenticationManager authenticationManager;
    private JwtTokenService jwtTokenService;
    
	
	public JwtAuthenticationResource(JwtTokenService jwtTokenService, AuthenticationManager authenticationManager) {
		this.jwtTokenService = jwtTokenService;
		this.authenticationManager = authenticationManager;
	}
	
    @PostMapping("/authenticate")
    public ResponseEntity<JwtTokenResponse> generateToken(@RequestBody JwtTokenRequest jwtTokenRequest) {
        try {
            var authenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                            jwtTokenRequest.username(), 
                            jwtTokenRequest.password());
            
            var authentication = 
                    authenticationManager.authenticate(authenticationToken);
            
            var token = jwtTokenService.createToken(authentication);
            
            return ResponseEntity.ok(new JwtTokenResponse(token));        	
        } catch (Exception e) {
            logger.error("Authentication failed", e);  // Log the exception with stack trace
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();       	
        }
    }

//	@PostMapping("/authenticate")
//	public JwtResponse authentication(Authentication authentication) {
//		return new JwtResponse(createToken(authentication));
//	}
	
}
