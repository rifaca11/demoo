package com.cires.technlogies.demo.services;

import com.cires.technlogies.demo.entities.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    List<UserEntity> getAllUsers();
    void saveUsers(List<UserEntity> users);
    List<UserEntity> findExistingUsers(List<UserEntity> users);

}