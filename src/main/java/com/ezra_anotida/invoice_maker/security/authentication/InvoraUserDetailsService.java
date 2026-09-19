package com.ezra_anotida.invoice_maker.security.authentication;

import com.ezra_anotida.invoice_maker.entity.User;
import com.ezra_anotida.invoice_maker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class InvoraUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Clock clock;

    public InvoraUserDetailsService(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        String normalizedEmail = normalizedEmail(email);

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        return InvoraUserDetails.from(user, clock.instant());
    }

    private String normalizedEmail(String email) {

        if(email == null || email.isBlank()){
            throw new UsernameNotFoundException("Invalid email or password");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
