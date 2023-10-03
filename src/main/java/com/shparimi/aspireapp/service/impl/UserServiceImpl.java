package com.shparimi.aspireapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shparimi.aspireapp.dao.UserDAO;
import com.shparimi.aspireapp.dao.models.Users;
import com.shparimi.aspireapp.exceptions.BadRequestException;
import com.shparimi.aspireapp.models.User;
import com.shparimi.aspireapp.security.UserDetailsImpl;
import com.shparimi.aspireapp.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {
	@Autowired
	private UserDAO userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return UserDetailsImpl.build(user);
	}

	public boolean isUser(int userId) {
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return userDetails.getId() == userId;
	}

	@Override
	public User signupUser(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new BadRequestException("User exists with given username");
		}
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new BadRequestException("User exists with given email");
		}
		log.info("Creating user with username {}", user.getUsername());
		Users userDao = Users.builder().username(user.getUsername()).password(user.getPassword()).email(user.getEmail())
				.role("ROLE_" + user.getRole().toUpperCase()).build();
		userDao = userRepository.save(userDao);
		return User.builder().id(userDao.getId()).email(userDao.getEmail()).role(userDao.getRole())
				.username(userDao.getUsername()).build();
	}
}
