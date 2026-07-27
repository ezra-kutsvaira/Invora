package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {

    Optional<CompanyProfile> findByActiveTrue ();
}
