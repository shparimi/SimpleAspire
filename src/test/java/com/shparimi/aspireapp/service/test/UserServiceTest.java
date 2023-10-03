package com.shparimi.aspireapp.service.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import com.shparimi.aspireapp.dao.UserDAO;
import com.shparimi.aspireapp.dao.models.Users;
import com.shparimi.aspireapp.exceptions.BadRequestException;
import com.shparimi.aspireapp.models.User;
import com.shparimi.aspireapp.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserDAO userRepository;

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(userServiceImpl, "userRepository", userRepository);
	}

	@Test
	public void testFindByUsername() {
		String username = "test";
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(Users.builder().username(username)
				.role("CUSTOMER").email("abc@abc.com").id(1).password("password").build()));
		UserDetails ud = userServiceImpl.loadUserByUsername(username);
		verify(userRepository, times(1)).findByUsername(username);
		assertTrue(ud.getUsername().equals(username));
	}

	@Test
	public void testFindByUsernameWithException() {
		String username = "test";
		when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(null));
		UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
				() -> userServiceImpl.loadUserByUsername(username));
		verify(userRepository, times(1)).findByUsername(username);
		assertTrue(ex.getMessage().equals("User Not Found with username: " + username));
	}

	@Test
	public void testSignUpUser() {
		when(userRepository.existsByUsername("username")).thenReturn(false);
		when(userRepository.existsByEmail("abc@abc.com")).thenReturn(false);
		User user = User.builder().email("abc@abc.com").password("password").role("CUSTOMER").username("username")
				.build();
		when(userRepository.save(any(Users.class))).thenReturn(
				Users.builder().email("abc@abc.com").id(1).role("ROLE_CUSTOMER").username("username").build());
		User result = userServiceImpl.signupUser(user);
		assertTrue(result.getId() == 1);
	}

	@Test
	public void testSignUpUserWithUsernameFail() {
		when(userRepository.existsByUsername("username")).thenReturn(true);
		User user = User.builder().username("username").build();
		BadRequestException ex = assertThrows(BadRequestException.class, ()-> userServiceImpl.signupUser(user));
		assertTrue(ex.getMessage().equals("User exists with given username"));
	}

	@Test
	public void testSignUpUserWithEmailFailFail() {
		when(userRepository.existsByUsername("username")).thenReturn(false);
		when(userRepository.existsByEmail("abc@abc.com")).thenReturn(true);
		User user = User.builder().username("username").email("abc@abc.com").build();
		BadRequestException ex = assertThrows(BadRequestException.class, ()-> userServiceImpl.signupUser(user));
		assertTrue(ex.getMessage().equals("User exists with given email"));
	}

}
