package com.realestate.backend.services.concretes;

import com.realestate.backend.core.services.JwtService;
import com.realestate.backend.core.utils.exceptions.ResourceNotFoundException;
import com.realestate.backend.dtos.requests.LoginRequestDTO;
import com.realestate.backend.dtos.requests.UserRequestDTO;
import com.realestate.backend.dtos.responses.ProductResponseDTO;
import com.realestate.backend.dtos.responses.UserResponseDTO;
import com.realestate.backend.entities.EmailVerificationToken;
import com.realestate.backend.entities.Role;
import com.realestate.backend.entities.User;
import com.realestate.backend.mappers.ProductMapper;
import com.realestate.backend.mappers.UserMapper;
import com.realestate.backend.repositories.EmailVerificationTokenRepository;
import com.realestate.backend.repositories.ProductRepository;
import com.realestate.backend.repositories.UserRepository;
import com.realestate.backend.services.MailService;
import com.realestate.backend.services.abstracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Override
    public UserResponseDTO registerUser(UserRequestDTO dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already taken");
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail().trim());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            user.setPassword(null);
        }

        user.setPhone(dto.getPhone());
        user.setRole(Role.CUSTOMER);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(savedUser);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        token.setUsed(false);

        emailVerificationTokenRepository.save(token);

        mailService.sendVerificationMail(savedUser.getEmail(), token.getToken());

        return userMapper.responseFromUser(savedUser);
    }


    @Override
    public List<UserResponseDTO> registerUsers(List<UserRequestDTO> list) {
        return list.stream()
                .map(this::registerUser)
                .toList();
    }







    @Override
    public Page<ProductResponseDTO> getUserProducts(Long userId, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return productRepository.findByUser(user, pageable)
                .map(productMapper::responseFromProduct);
    }

    @Override
    public UserResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {

        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        if (!user.isEmailVerified()) {
            throw new IllegalStateException("Email not verified");
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        UserResponseDTO responseDTO = userMapper.responseFromUser(user);
        responseDTO.setToken(token);

        return responseDTO;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::responseFromUser)
                .toList();
    }

    @Override
    public UserResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.responseFromUser(user);
    }

    @Override
    public UserResponseDTO updateUserProfile(
            Long userId,
            UserRequestDTO userRequestDTO
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        userMapper.updateUserFromRequest(userRequestDTO, user);

        if (
                userRequestDTO.getPassword() != null &&
                        !userRequestDTO.getPassword().isBlank()
        ) {
            user.setPassword(
                    passwordEncoder.encode(userRequestDTO.getPassword())
            );
        }

        User updatedUser = userRepository.save(user);

        return userMapper.responseFromUser(updatedUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}