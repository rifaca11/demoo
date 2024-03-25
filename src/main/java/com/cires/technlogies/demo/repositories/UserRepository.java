package com.cires.technlogies.demo.repositories;

import com.cires.technlogies.demo.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByEmailInOrUsernameIn(List<String> emails, List<String> usernames);
    public UserEntity findByUsername(String username);

}
