package com.shparimi.aspireapp.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shparimi.aspireapp.dao.models.Users;

public interface UserDAO extends JpaRepository<Users, Integer> {
	Optional<Users> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}
