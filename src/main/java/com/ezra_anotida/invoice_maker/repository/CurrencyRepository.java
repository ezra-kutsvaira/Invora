package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    List<Currency> findByActiveTrue();

    Optional<Currency> findByCodeIgnoreCase (String code);

    boolean existsByCodeIgnoreCase (String code);

    Optional<Currency> findByDefaultCurrencyTrue();
}
