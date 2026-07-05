package repository;

import entity.Currency;

import java.util.*;

public interface CurrencyRepository {

    List<Currency> findByActiveTrue();

    Optional<Currency> findByCode (String code);

    boolean existsByCode (String code);
}
