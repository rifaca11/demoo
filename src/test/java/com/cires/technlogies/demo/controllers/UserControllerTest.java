package com.cires.technlogies.demo.controllers;
import com.cires.technlogies.demo.dto.UserDto;
import com.cires.technlogies.demo.mapper.IMapperDto;

import com.cires.technlogies.demo.entities.UserEntity;
import com.cires.technlogies.demo.repositories.UserRepository;
import com.cires.technlogies.demo.services.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private Authentication authentication;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;
    @Mock
    private IMapperDto<UserDto, UserEntity> userMapper; // Mock IMapperDto
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IUserService userService;

    @Test
    void testGenerateUsers() {
        // Mocking UserRepository save method
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());

        // Test generateUsers method
        ResponseEntity<String> responseEntity = userController.generateUsers(5);

        // Verify userRepository save method is called
        verify(userRepository, times(5)).save(any(UserEntity.class));

        // Verify response status code is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Check if response body is not null
        String responseBody = responseEntity.getBody();
        assert responseBody != null;

    }

    @Test
    void testUploadUsersBatch() throws IOException {
        // Mocking userService methods
        when(userService.findExistingUsers(any())).thenReturn(Collections.emptyList());

        // Mocking passwordEncoder encode method
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        // Prepare test data
        String jsonContent = "[{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\"}]";
        MockMultipartFile file = new MockMultipartFile("file", "users.json", "application/json", jsonContent.getBytes());

        // Mocking IMapperDto convertListToListEntity method
        when(userMapper.convertListToListEntity(anyList(), eq(UserEntity.class))).thenReturn(Collections.singletonList(new UserEntity()));

        // Test uploadUsersBatch method
        ResponseEntity<String> responseEntity = userController.uploadUsersBatch(file);

        // Verify userService saveUsers method is called
        verify(userService, times(1)).saveUsers(any());

        // Verify response status code is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Check if response body contains expected content
        String expectedResponse = "{\"totalRecords\": 1, \"importedRecords\": 1, \"failedRecords\": 0}";
        assertEquals(expectedResponse, responseEntity.getBody());
    }


    @Test
    void testGetCurrentUser() {
        // Mock the principal (user details)
        UserDetails userDetails = User.withUsername("testUser").password("password").roles("ROLE").build();
        // Create an authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        // Mock the authentication object in the controller
        when(this.authentication.getPrincipal()).thenReturn(authentication.getPrincipal());

        // Test getCurrentUser method
        ResponseEntity<String> responseEntity = userController.getCurrentUser(this.authentication);

        // Verify response status code is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify response body contains expected content
        assertEquals("My profile: testUser", responseEntity.getBody());
    }


    @Test
    void testGetUserProfile() {
        // Define a username for testing
        String username = "testUser";

        // Test getUserProfile method
        ResponseEntity<String> responseEntity = userController.getUserProfile(username);

        // Verify response status code is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify response body contains expected content
        assertEquals("Profile of user:" + username, responseEntity.getBody());
    }

}
