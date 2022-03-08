package com.animeit.AnimeIT.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.animeit.AnimeIT.exception.TokenInvalidException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import static java.util.Date.from;

@Service
@Slf4j
public class JwtProvider {
	
	private KeyStore keyStore;
	@Value("${jwt.expiration.time}")
	public Long jwtExpirationInMillis;
	
	@PostConstruct
	public void init() {
		try {
			keyStore = KeyStore.getInstance("JKS");
			InputStream resourceAsStream = getClass().getResourceAsStream("/codeKey.jks");
			keyStore.load(resourceAsStream, "secret".toCharArray());
		} catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
			throw new RuntimeException("Exception occured during loading the keyStore, message-"+e.getMessage());
		}
	}
	
	public String generateToken(Authentication authentication) {
		User principal = (User) authentication.getPrincipal();
		log.info("Username "+ principal.getUsername()+ " Password "+ principal.getPassword());
		return Jwts.builder()
				.setSubject(principal.getUsername())
				.setIssuedAt(from(Instant.now()))
				.signWith(getPrivateKey())
				.setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
				.compact();
	}
	
	public String generateTokenWithUsername(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(from(Instant.now()))
				.signWith(getPrivateKey())
				.setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
				.compact();
	}

	private PrivateKey getPrivateKey() {
		try {
			return (PrivateKey) keyStore.getKey("codeKey", "secret".toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new RuntimeException("Exception occured during retrieving key, message: "+e.getMessage());
		}
	}
	
	public boolean validateJwtToken(String token) {
		Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token);
		return true;
	}

	private PublicKey getPublicKey() {
		try {
			return keyStore.getCertificate("codeKey").getPublicKey();
		} catch (Exception e) {
			throw new TokenInvalidException("Exception occured while retrieving public key from keystore");
		}
	}
	
	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parser()
							.setSigningKey(getPublicKey())
							.parseClaimsJws(token)
							.getBody();
		return claims.getSubject();
	}
}
