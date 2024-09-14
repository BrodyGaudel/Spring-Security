package org.mounanga.securityservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.securityservice.dto.MailDTO;
import org.mounanga.securityservice.dto.ResetPasswordRequestDTO;
import org.mounanga.securityservice.dto.VerificationRequestDTO;
import org.mounanga.securityservice.entity.User;
import org.mounanga.securityservice.entity.Verification;
import org.mounanga.securityservice.exception.UserNotFoundException;
import org.mounanga.securityservice.exception.VerificationCodeExpiredException;
import org.mounanga.securityservice.exception.VerificationNotFoundException;
import org.mounanga.securityservice.repository.UserRepository;
import org.mounanga.securityservice.repository.VerificationRepository;
import org.mounanga.securityservice.service.VerificationService;
import org.mounanga.securityservice.util.CodeGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailingService mailingService;

    public VerificationServiceImpl(VerificationRepository verificationRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, MailingService mailingService) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailingService = mailingService;
    }

    @Override
    public void requestVerification(@NotNull VerificationRequestDTO verificationRequestDTO) {
        log.info("Inside requestVerification()");
        User user = getUserByEmail(verificationRequestDTO.email());

        Verification verification = new Verification();
        verification.setCode(CodeGenerator.generateVerificationCode());
        verification.setEmail(user.getEmail());
        verification.setExpires(LocalDateTime.now().plusMinutes(30));
        Verification savedVerification = verificationRepository.save(verification);
        sendVerificationEmail(savedVerification);
        log.info("Verification sent successful");
    }

    @Override
    public void resetPassword(@NotNull ResetPasswordRequestDTO resetPasswordRequestDTO) {
        log.info("Inside resetPassword()");
        Verification verification = verificationRepository.findByCodeAndEmail(
                resetPasswordRequestDTO.code(), resetPasswordRequestDTO.email()
        ).orElseThrow(() -> new VerificationNotFoundException("verification code not found"));

        if(verification.getExpires().isBefore(LocalDateTime.now())) {
            throw new VerificationCodeExpiredException("code expired. Please try ask for another verification's code");
        }
        verificationRepository.delete(verification);
        updatePassword(resetPasswordRequestDTO.email(), resetPasswordRequestDTO.newPassword());
        log.info("Password reset successful");
        sendPasswordUpdatedEmail(resetPasswordRequestDTO.email());
    }

    private void sendPasswordUpdatedEmail(String email) {
        String body = "Hello, your password has been updated successfully";
        MailDTO mail = new MailDTO(email, "Password Updated", body);
        mailingService.send(mail);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private void sendVerificationEmail(@NotNull Verification verification) {
        String body = String.format("Hello, here is your verification code: %s. it expires in 30 minutes", verification.getCode());
        MailDTO mail = new MailDTO(verification.getEmail(), "Verification code", body);
        mailingService.send(mail);
    }

    private void updatePassword(String email, String password) {
        User user = getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
