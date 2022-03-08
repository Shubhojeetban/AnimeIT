package com.animeit.AnimeIT.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;
	
	@NotBlank(message = "Username is required")
	private String userName;
	
	@NotBlank(message = "Password is required")
	private String password;
	
	@Email
	@NotBlank(message = "Email is required")
	// @UniqueElements(message = "Email already in use")
	private String email;
	
	private Instant createdDate;
	private Boolean enable;
}
