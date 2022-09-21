package com.a205.mafya.api.service;

import com.a205.mafya.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {

        return userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new NoSuchElementException("No user exist. exception from CustomUserDetailService"));


    }

}
