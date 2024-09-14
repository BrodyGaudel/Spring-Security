package org.mounanga.securityservice.repository;

import org.mounanga.securityservice.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByPersonalIdentificationNumber(String personalIdentificationNumber);
}
