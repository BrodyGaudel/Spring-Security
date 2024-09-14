package org.mounanga.securityservice.service;

import org.mounanga.securityservice.dto.ResetPasswordRequestDTO;
import org.mounanga.securityservice.dto.VerificationRequestDTO;

public interface VerificationService {

    void requestVerification(VerificationRequestDTO verificationRequestDTO);
    void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}
