package com.dtw.user.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtw.user.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String name);
}