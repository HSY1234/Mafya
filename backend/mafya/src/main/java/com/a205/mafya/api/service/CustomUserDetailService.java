package com.a205.mafya.api.service;

import com.a205.mafya.db.entity.Manager;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.ManagerRepository;
import com.a205.mafya.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUserCode(userCode);
        Optional<Manager> manager = managerRepository.findByManagerCode(userCode);

        if(user.isPresent()) {
            return user.get();
        }

        if(manager.isPresent()){
            User user_managerForm = User.builder()
                    .userCode(manager.get().getManagerCode())
                    .password(manager.get().getPassword())
                    .build();
            return user_managerForm;
        }

        throw new NoSuchElementException("No user exist. exception from CustomUserDetailService");


    }

}
