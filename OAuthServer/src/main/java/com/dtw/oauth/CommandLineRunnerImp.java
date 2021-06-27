package com.dtw.oauth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.dtw.oauth.entity.Role;
import com.dtw.oauth.enums.RoleEnum;
import com.dtw.oauth.repo.RoleRepo;

@Component
public class CommandLineRunnerImp implements CommandLineRunner {

	@Autowired
	private RoleRepo roleRepo;
	
	@Override
	public void run(String... args) throws Exception {
		
		List<Role> storedRoles = roleRepo.findAll();
		
		for(RoleEnum roleEnum : RoleEnum.values()) {
			if(!containsByName(storedRoles, roleEnum)) {
				roleRepo.save(Role.builder().name(roleEnum).build());
			}
		}
	}
	
	public boolean containsByName(List<Role> roles, RoleEnum name) {
		for(Role role : roles) {
			if(role.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
}