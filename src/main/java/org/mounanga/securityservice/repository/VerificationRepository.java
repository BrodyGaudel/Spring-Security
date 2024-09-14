package org.mounanga.securityservice.repository;

import org.mounanga.securityservice.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, String> {

    @Query("select v from Verification v where v.email like :email and v.code like :code")
    Optional<Verification> findByCodeAndEmail(@Param("code") String code, @Param("email") String email);

    @Query("select v from Verification v where v.expires < :now")
    List<Verification> findExpired(@Param("now") LocalDateTime now);
}
