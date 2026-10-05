package com.realestate.backend.controller;

import com.realestate.backend.core.utils.exceptions.ResourceNotFoundException;
import com.realestate.backend.dtos.requests.LoginRequestDTO;
import com.realestate.backend.dtos.requests.UserRequestDTO;
import com.realestate.backend.dtos.responses.LoginResponseDTO;
import com.realestate.backend.dtos.responses.ProductResponseDTO;
import com.realestate.backend.dtos.responses.UserResponseDTO;
import com.realestate.backend.services.abstracts.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO loginRequestDTO) {

        try {
            UserResponseDTO userResponse = userService.loginUser(loginRequestDTO);

            LoginResponseDTO loginResponse = new LoginResponseDTO();
            loginResponse.setToken(userResponse.getToken());
            loginResponse.setId(userResponse.getId().toString());
            loginResponse.setRole(userResponse.getRole());

            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO());

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new LoginResponseDTO());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponseDTO());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody @Valid UserRequestDTO userRequestDTO) {

        UserResponseDTO response = userService.registerUser(userRequestDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/register/bulk")
    public ResponseEntity<List<UserResponseDTO>> registerBulk(
            @RequestBody List<UserRequestDTO> list) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUsers(list));
    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(
            @PathVariable Long userId) {

        return ResponseEntity.ok(userService.getUserProfile(userId));
    }


    @GetMapping("/{userId}/products")
    public ResponseEntity<Page<ProductResponseDTO>> getUserProducts(
            @PathVariable Long userId,
            Pageable pageable) {

        return ResponseEntity.ok(
                userService.getUserProducts(userId, pageable)
        );
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDTO> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserRequestDTO userRequestDTO) {

        return ResponseEntity.ok(
                userService.updateUserProfile(userId, userRequestDTO)
        );
    }
}