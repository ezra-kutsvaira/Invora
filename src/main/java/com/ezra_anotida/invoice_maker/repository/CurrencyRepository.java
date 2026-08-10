package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByIdAndOrganizationId(Long currencyId, Long organizationId);
    Optional<Currency> findByOrganizationIdAndCodeIgnoreCase(Long organizationId, String code);
    Optional<Currency> findByOrganizationIdAndDefaultCurrencyTrue(Long organizationId);
    List<Currency> findByOrganizationId(Long organizationId);
    List<Currency> findByOrganizationIdAndActiveTrue(Long organizationId);
    boolean existsByOrganizationIdAndCodeIgnoreCase(Long organizationId, String code);
}
