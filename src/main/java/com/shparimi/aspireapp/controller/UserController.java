package com.shparimi.aspireapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.shparimi.aspireapp.exceptions.BadRequestException;
import com.shparimi.aspireapp.exceptions.WebApplicationException;
import com.shparimi.aspireapp.jwt.JwtUtils;
import com.shparimi.aspireapp.models.LoginRequest;
import com.shparimi.aspireapp.models.SignUpRequest;
import com.shparimi.aspireapp.models.User;
import com.shparimi.aspireapp.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserController {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	private enum UserTypes {
		CUSTOMER, MANAGER
	}

	@PostMapping("/signin")
	@ResponseBody
	public String authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			String jwt = jwtUtils.generateJwtToken(authentication);
			log.debug("Successfully created the JWT for the user {}", loginRequest.getUsername());
			return jwt;
		} catch (Exception ex) {
			log.error("Error while trying to generate JWT", ex);
			if (ex instanceof AuthenticationException) {
				throw ex;
			}
			throw new WebApplicationException(ex.getMessage());
		}
	}

	@PostMapping("/signup")
	@ResponseBody
	public User registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		try {
			if (!signUpRequest.getRole().equalsIgnoreCase(UserTypes.MANAGER.toString())
					&& !signUpRequest.getRole().equalsIgnoreCase(UserTypes.CUSTOMER.toString())) {
				throw new BadRequestException("Role can be of only two types, with CUSTOMER or MANAGER");
			}
			User user = new User(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()),
					signUpRequest.getEmail(), signUpRequest.getRole().toString());
			user = userService.signupUser(user);
			log.info("Successfully created the user with id {}", user.getId());
			return user;
		} catch (Exception ex) {
			log.error("Error while trying to create user", ex);
			if (ex instanceof BadRequestException) {
				throw ex;
			}
			throw new WebApplicationException(ex.getMessage());
		}
	}
}
