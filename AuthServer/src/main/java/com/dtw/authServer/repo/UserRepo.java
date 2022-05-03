package com.dtw.authServer.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtw.authServer.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String name);
}