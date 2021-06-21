package com.dtw.oauth.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtw.oauth.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
}