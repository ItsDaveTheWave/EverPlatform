package com.dtw.user.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtw.user.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String name);
	Optional<User> findByEmail(String email);
}