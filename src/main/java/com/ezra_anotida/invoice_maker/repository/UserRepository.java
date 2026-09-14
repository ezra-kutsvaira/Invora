package com.ezra_anotida.invoice_maker.repository;

import com.ezra_anotida.invoice_maker.entity.User;
import com.ezra_anotida.invoice_maker.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCaseAndStatus(String email, UserStatus userStatus);

    Optional<User> findByIdAndStatus(Long userId, UserStatus status);

    boolean existsByEmailIgnoreCase(String email);
}
