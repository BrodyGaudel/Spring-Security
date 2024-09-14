package org.mounanga.securityservice.configuration;

import lombok.extern.slf4j.Slf4j;
import org.mounanga.securityservice.entity.Verification;
import org.mounanga.securityservice.repository.VerificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class MyScheduledTask {

    private final VerificationRepository verificationRepository;

    public MyScheduledTask(VerificationRepository verificationRepository) {
        this.verificationRepository = verificationRepository;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void performTask() {
        log.info("Task executed using cron at: {}", System.currentTimeMillis());
        List<Verification> verifications = verificationRepository.findExpired(LocalDateTime.now());
        verificationRepository.deleteAll(verifications);
        log.info("Task executed successfully");
    }

}
