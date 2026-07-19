package com.realestate.backend.services.concretes;

import com.realestate.backend.dtos.requests.ForgotPasswordRequest;
import com.realestate.backend.dtos.requests.ResetPasswordRequest;
import com.realestate.backend.dtos.responses.ForgotPasswordResponse;
import com.realestate.backend.dtos.responses.ResetPasswordResponse;
import com.realestate.backend.entities.ResetPasswordToken;
import com.realestate.backend.entities.User;
import com.realestate.backend.mappers.ResetPasswordMapper;
import com.realestate.backend.repositories.ResetPasswordTokenRepository;
import com.realestate.backend.repositories.UserRepository;
import com.realestate.backend.services.MailService;
import com.realestate.backend.services.abstracts.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ResetPasswordToken token = ResetPasswordToken.create(user, 15);
        tokenRepository.save(token);
        mailService.sendResetPasswordMail(user.getEmail(), token.getToken());
        // mail gönderimi burada
        return new ForgotPasswordResponse ("Password reset link sent");
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {

        ResetPasswordToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!ResetPasswordMapper.isTokenValid(token)) {
            throw new RuntimeException("Token expired or already used");
        }


        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        token.setUsed(true);

        userRepository.save(user);
        tokenRepository.save(token);


        return ResetPasswordMapper.toResponse("Password updated successfully");
    }
}