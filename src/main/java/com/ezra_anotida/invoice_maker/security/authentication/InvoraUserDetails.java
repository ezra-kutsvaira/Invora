package com.ezra_anotida.invoice_maker.security.authentication;

import com.ezra_anotida.invoice_maker.entity.User;
import com.ezra_anotida.invoice_maker.enums.UserStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class InvoraUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private  final Long userId;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final UserStatus userStatus;
    private final boolean accountNonLocked;

    public InvoraUserDetails(Long userId, String username, String email, String passwordHash, UserStatus userStatus, boolean accountNonLocked) {
        this.userId = Objects.requireNonNull(userId, "User id is required");
        this.name = Objects.requireNonNull(username, "Username is required");
        this.email = Objects.requireNonNull(email, "email is required");;
        this.passwordHash = Objects.requireNonNull(passwordHash, "password is required");
        this.userStatus = Objects.requireNonNull(userStatus, "User status is required");;
        this.accountNonLocked = accountNonLocked;
    }

    public static InvoraUserDetails from(User user, Instant currentTime){

        Objects.requireNonNull(user, "User is required");
        Objects.requireNonNull(currentTime, "Current time is required");

        boolean notPermanentlyLocked = user.getStatus()!= UserStatus.LOCKED;

        boolean temporaryLockExpired = user.getLockedUntil() == null || !user.getLockedUntil().isAfter(currentTime);

        boolean accountNonLocked = notPermanentlyLocked && temporaryLockExpired;

        return new InvoraUserDetails(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getStatus(),
                accountNonLocked
        );
    }

    public Long getUserId(){
        return userId;
    }

    public String getDisplayName(){
        return name;
    }

    public UserStatus getStatus(){
        return userStatus;
    }

    //Obtained from the OrganizationMembership
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    //Returning a HashPassword
    @Override
    public String getPassword() {
        return passwordHash;
    }

    //Using an email as the Spring security username
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return userStatus == UserStatus.ACTIVE;
    }

}
