package repository;

import entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TaxRateRepository extends JpaRepository<TaxRate,Long> {

    List<TaxRate> findByActiveTrue();

    Optional<TaxRate> findByTaxName (String taxName);

    boolean existsByTaxName (String taxName);
}
