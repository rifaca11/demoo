package com.cires.technlogies.demo.controllers;

import com.cires.technlogies.demo.dto.UserDto;
import com.cires.technlogies.demo.entities.UserEntity;
import com.cires.technlogies.demo.mapper.IMapperDto;
import com.cires.technlogies.demo.repositories.UserRepository;
import com.cires.technlogies.demo.services.IUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.github.javafaker.Faker;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final IUserService userService;
    private final IMapperDto<UserDto, UserEntity> userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, IUserService userService, IMapperDto<UserDto, UserEntity> userMapper,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Generate Users", description = "Generate fake user data and return as JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    // la bibliothèque Faker pour générer des données réalistes pour chaque utilisateur.
    @GetMapping(value = "/api/users/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateUsers(@RequestParam("count") int count) {

        Faker faker = new Faker();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < count; i++) {
            UserEntity user = new UserEntity();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setBirthDate(dateFormat.format(faker.date().birthday()));
            user.setCity(faker.address().city());
            user.setCountry(faker.address().countryCode());
            user.setAvatar(faker.internet().avatar());
            user.setCompany(faker.company().name());
            user.setJobPosition(faker.job().title());
            user.setMobile(faker.phoneNumber().cellPhone());
            user.setUsername(faker.name().username());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(faker.internet().password(6,10));
            user.setRole(faker.bool().bool() ? "admin" : "role");
            userRepository.save(user);
        }

        // Retrieve generated users from the database
        List<UserEntity> users = userRepository.findAll();

        // Convert users to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(users);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la conversion en JSON");
        }

        // Prepare headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("filename", "users.json");

        return new ResponseEntity<>(jsonString, headers, HttpStatus.OK);
    }


    @PostMapping(value = "/api/users/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadUsersBatch(@RequestPart("file") MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convert the uploaded JSON file to a list of UserDto objects
            List<UserDto> userDtoList = objectMapper.readValue(file.getBytes(), new TypeReference<>() {
            });

            // Convert UserDto objects to UserEntity objects
            List<UserEntity> userEntityList = userMapper.convertListToListEntity(userDtoList, UserEntity.class);

            // Encode passwords before saving
            for (UserEntity userEntity : userEntityList) {
                String rawPassword = userEntity.getPassword();
                String encodedPassword = passwordEncoder.encode(rawPassword);
                userEntity.setPassword(encodedPassword);
            }

            // Check for duplicates
            List<UserEntity> existingUsers = userService.findExistingUsers(userEntityList);
            userEntityList.removeAll(existingUsers);

            // Save users in the database
            userService.saveUsers(userEntityList);

            // Prepare response JSON
            int totalRecords = userDtoList.size();
            int importedRecords = userEntityList.size();
            int failedRecords = totalRecords - importedRecords;
            String response = String.format("{\"totalRecords\": %d, \"importedRecords\": %d, \"failedRecords\": %d}", totalRecords, importedRecords, failedRecords);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file.");
        }
    }
    }

