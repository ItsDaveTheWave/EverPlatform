package com.dtw.oauth.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dtw.oauth.entity.Role;
import com.dtw.oauth.enums.RoleEnum;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleEnum name);
}