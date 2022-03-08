package com.animeit.AnimeIT.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.animeit.AnimeIT.dto.AuthenticationResponse;
import com.animeit.AnimeIT.dto.LoginRequest;
import com.animeit.AnimeIT.dto.RefreshTokenRequest;
import com.animeit.AnimeIT.dto.RegisterRequest;
import com.animeit.AnimeIT.exception.InvalidTokenException;
import com.animeit.AnimeIT.exception.TokenInvalidException;
import com.animeit.AnimeIT.model.NotificationEmail;
import com.animeit.AnimeIT.model.User;
import com.animeit.AnimeIT.model.VerificationToken;
import com.animeit.AnimeIT.repository.UserRepository;
import com.animeit.AnimeIT.repository.VerficationTokenRepository;
import com.animeit.AnimeIT.security.JwtProvider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
	
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerficationTokenRepository verfiRepository;
	private final MailService mailService;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenService refreshTokenService;
	
	@Transactional
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUserName(registerRequest.getUserName());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setCreatedDate(Instant.now());
		user.setEnable(false);
		
		userRepository.save(user);
		
		String token = generateVerificationToken(user);
		NotificationEmail notificationEmail = new NotificationEmail();
		notificationEmail.setRecipient(registerRequest.getEmail());
		notificationEmail.setSubject("Activation Link");
		
		String BASEURL = ServletUriComponentsBuilder.fromCurrentContextPath().replacePath(null).build().toString();
		String SUBJECT = "Please Activate your Account";
		
		String message = "Thank you!! For signing up in Code Reddit. Please click in the link to activate your account " 
						+ BASEURL + "/api/auth/accountVerification/"+token;
		mailService.sendMail(new NotificationEmail(SUBJECT, user.getEmail(), message));
	}

	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		VerificationToken verfiToken = new VerificationToken();
		verfiToken.setToken(token);
		verfiToken.setUser(user);
		verfiToken.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
		
		verfiRepository.save(verfiToken);
		return token;
	}

	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verfiRepository.findByToken(token);
		verificationToken.orElseThrow(() -> new InvalidTokenException("Invalid Token"));
		
		// If the Token get expired
		if(verificationToken.get().getExpiryDate().isBefore(Instant.now()))
			throw new TokenInvalidException("Token Expired!");
		fetchUserAndEnable(verificationToken.get());
	}

	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String email = verificationToken.getUser().getEmail();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not found with email- "+email));
		user.setEnable(true);
		userRepository.saveAndFlush(user);
	}
	public AuthenticationResponse login(LoginRequest loginRequest) {
		// Returns Authentication after internally authenticating with UserDetailsService (which checks with the DB)
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		log.info("Authentication Service, Username: "+authentication.getPrincipal()+" Password: "+authentication.getCredentials());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwtToken = jwtProvider.generateToken(authentication);
		return AuthenticationResponse.builder().authenticationToken(jwtToken)
									.refreshToken(refreshTokenService.generateRefreshToken().getToken())
									.expiresAt(Instant.now().plusMillis(jwtProvider.jwtExpirationInMillis))
									.username(loginRequest.getUsername())
									.build();
									
	}
	
	@Transactional(readOnly = true)
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = (String)authentication.getPrincipal();
		return userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("Username "+ username +" not found"));
	}

	public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
		refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
		String token = jwtProvider.generateTokenWithUsername(refreshTokenRequest.getUsername());
		return AuthenticationResponse.builder().authenticationToken(token)
				.refreshToken(refreshTokenRequest.getRefreshToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.jwtExpirationInMillis))
				.username(refreshTokenRequest.getUsername())
				.build();
	}
}
