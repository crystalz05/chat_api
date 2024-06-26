package com.chat.app.chatApi.jwt.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.chat.app.chatApi.repository.UserRepository;
import com.chat.app.chatApi.user.ChatUser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class JwtSecurityConfiguration {
	
	
	private UserRepository userRepository;
	
	public JwtSecurityConfiguration(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.authorizeHttpRequests(auth -> {
	        auth
	            .requestMatchers("/authenticate", "/create/users").permitAll()  // Allow all requests to /authenticate without authentication
	            .anyRequest().authenticated();  // Enforce authentication for all other requests
	    });
	    http.sessionManagement(session ->{
	        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	    });
//	    http.formLogin(withDefaults());
//	    http.httpBasic(withDefaults());
	    http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
	    http.csrf(csrf -> csrf
	            .ignoringRequestMatchers("/authenticate", "/create/users")
	        );
	    
	    return http.build();
	}
	
	
//    @Bean
//    public UserDetailsService userDetailsService2() {
//        UserDetails user = User.withUsername("mikebingp@gmail.com")
//                                .password("{noop}1234")
//                                .authorities("read")
//                                .roles("USER")
//                                .build();
//        
//        UserDetails user1 = User.withUsername("philip")
//                .password("{noop}1234")
//                .authorities("read")
//                .roles(Roles.ADMIN.toString())
//                .build();
//
//        return new InMemoryUserDetailsManager(user, user1);
//    }


//	@Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user1 = User.withUsername("tyro")
//                                .password(passwordEncoder().encode("1234"))
//                                .authorities(Roles.USER.toString())
//                                .build();
//        
//        UserDetails user2 = User.withUsername("admin")
//                                .password(passwordEncoder().encode("1234"))
//                                .authorities(Roles.ADMIN.toString())
//                                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2);
//    }
	
    @Bean   
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
	
//    @Bean
//    public PasswordEncoder passwordEncoder1() {
//        // Use NoOpPasswordEncoder for plain text passwords (not recommended for production)
//        return NoOpPasswordEncoder.getInstance();
//    }

    @Bean
    public UserDetailsService userDetailsService() {
    	return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
				ChatUser chatUser = userRepository.findByEmail(email);
				if (chatUser == null) {
					throw new UsernameNotFoundException("User not found with email: "+email);
				}
				return User.withUsername(chatUser.getEmail())
						.password(chatUser.getPassword())
						.authorities(Roles.USER.toString())
						.build();
			}
    	};
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
    
    @Bean
    public KeyPair keyPair(){
    	KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	    	keyPairGenerator.initialize(2048);
	    	return  keyPairGenerator.generateKeyPair();
	    	
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    @Bean
    public RSAKey rsaKey(KeyPair keyPair) {
    	
    	return new RSAKey
    			.Builder((RSAPublicKey)keyPair.getPublic())
    			.privateKey(keyPair.getPrivate())
    			.keyID(UUID.randomUUID().toString())
    			.build();
    }
    
    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
    	var jwkSet = new JWKSet(rsaKey);
    	
		return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    	
    }
    
  @Bean
  public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
  	return NimbusJwtDecoder
  			.withPublicKey(rsaKey.toRSAPublicKey())
  			.build();
  }
  
  @Bean
  public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
	  return new NimbusJwtEncoder(jwkSource);
  }
    
}

