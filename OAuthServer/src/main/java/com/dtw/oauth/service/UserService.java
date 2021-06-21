package com.dtw.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dtw.oauth.repo.UserRepo;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User [" + username + "] not found"));
	}
}