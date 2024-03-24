package com.cires.technlogies.demo.services;

import com.cires.technlogies.demo.dto.CustomUserDetails;
import com.cires.technlogies.demo.entities.UserEntity;
import com.cires.technlogies.demo.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void saveUsers(List<UserEntity> users) {
        userRepository.saveAll(users);
    }

    @Override
    public List<UserEntity> findExistingUsers(List<UserEntity> users) {
        List<String> emails = users.stream().map(UserEntity::getEmail).collect(Collectors.toList());
        List<String> usernames = users.stream().map(UserEntity::getUsername).collect(Collectors.toList());

        return userRepository.findByEmailInOrUsernameIn(emails, usernames);
    }

}