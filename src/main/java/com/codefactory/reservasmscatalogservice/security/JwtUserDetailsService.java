package com.codefactory.reservasmscatalogservice.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Since Catalog Service doesn't have a user database, we return a basic UserDetails
        // The actual authorities are extracted from JWT claims in JwtAuthenticationFilter
        return User.builder()
                .username(email)
                .password("") // No password needed for JWT validation
                .authorities(Collections.emptyList())
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
