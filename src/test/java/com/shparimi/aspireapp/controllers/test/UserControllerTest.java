package com.shparimi.aspireapp.controllers.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.shparimi.aspireapp.controller.UserController;
import com.shparimi.aspireapp.exceptions.BadRequestException;
import com.shparimi.aspireapp.exceptions.WebApplicationException;
import com.shparimi.aspireapp.jwt.JwtUtils;
import com.shparimi.aspireapp.models.LoginRequest;
import com.shparimi.aspireapp.models.SignUpRequest;
import com.shparimi.aspireapp.models.User;
import com.shparimi.aspireapp.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@Mock
	private PasswordEncoder encoder;

	@Mock
	private UserService userService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtils jwtUtils;

	@InjectMocks
	private UserController userController;

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(userController, "encoder", encoder);
		ReflectionTestUtils.setField(userController, "userService", userService);
		ReflectionTestUtils.setField(userController, "authenticationManager", authenticationManager);
		ReflectionTestUtils.setField(userController, "jwtUtils", jwtUtils);
	}

	@Test
	public void testUserCreation() throws Exception{
		when(userService.signupUser(any(User.class))).thenReturn(User.builder().id(1).build());
		SignUpRequest request = new SignUpRequest();
		request.setRole("CUSTOMER");
		User user = userController.registerUser(request);
		verify(userService, times(1)).signupUser(any(User.class));
		assertTrue(user.getId()==1);
	}

	@Test
	public void testUserCreationRoleFail() throws Exception {
		SignUpRequest request = new SignUpRequest();
		request.setRole("TEST");
		BadRequestException ex = assertThrows(BadRequestException.class, () -> userController.registerUser(request));
		assertEquals("Role can be of only two types, with CUSTOMER or MANAGER", ex.getMessage());
	}

	@Test
	public void testUserCreationServiceFail() throws Exception{
		when(userService.signupUser(any(User.class))).thenThrow(RuntimeException.class);
		SignUpRequest request = new SignUpRequest();
		request.setRole("CUSTOMER");
		assertThrows(WebApplicationException.class, ()-> userController.registerUser(request));
	}
	
	@Test
	public void testJWTCreation() throws Exception{
		when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
		when(jwtUtils.generateJwtToken(null)).thenReturn("123");
		LoginRequest lr = new LoginRequest();
		lr.setPassword("abc123");
		lr.setUsername("223456");
		String jwt = userController.authenticateUser(lr);
		assertTrue(jwt.equals("123"));
		verify(authenticationManager,times(1)).authenticate(any(Authentication.class));
		verify(jwtUtils, times(1)).generateJwtToken(null);
	}
	
	@Test
	public void testJWTCreationWithAuthException() throws Exception{
		when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(RuntimeException.class);
		LoginRequest lr = new LoginRequest();
		lr.setPassword("abc123");
		lr.setUsername("223456");
		assertThrows(WebApplicationException.class, ()-> userController.authenticateUser(lr));
		verify(authenticationManager,times(1)).authenticate(any(Authentication.class));
	}
	

}
