package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table (name = "company_profiles")
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Column(nullable = false)
    private String companyName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @NotBlank(message ="Phone number is required")
    private String phoneNumber; //Validation


    private String website;

    @NotBlank(message = "Address is required")
    @Column(length = 2000, nullable = false)
    private String address;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "vatNumber is required")
    @Column(nullable = false)
    private String vatNumber;

    @NotBlank(message = "tinNumber is required")
    @Column(nullable = false)
    private String tinNumber;

    private String bankAccountName;

    private String bankAccountNumber;

    private String bankBranch;

    private String bankSwiftCode;

    @Column(length = 2000)
    private String invoiceTerms;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    //Getters and Setters

}
