package org.mounanga.securityservice.restcontroller;

import lombok.extern.slf4j.Slf4j;
import org.mounanga.securityservice.dto.ResetPasswordRequestDTO;
import org.mounanga.securityservice.dto.VerificationRequestDTO;
import org.mounanga.securityservice.service.VerificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verifications")
public class VerificationRestController {

    private final VerificationService verificationService;

    public VerificationRestController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/request")
    public void requestVerification(@RequestBody VerificationRequestDTO verificationRequestDTO) {
        verificationService.requestVerification(verificationRequestDTO);
    }


    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        verificationService.resetPassword(resetPasswordRequestDTO);
    }
}
