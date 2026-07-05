package dto.company;
import jakarta.validation.constraints.*;

public record CreateCompanyProfileRequest(

    @NotBlank (message = "Company name is required")
    @Size(max = 150)
    String companyName,

    @NotBlank(message = "Email is required")
    @Email (message = "Invalid email address")
    String email,

    @NotBlank (message = "Phone number is required")
    String phone,

    String website,

    @NotBlank (message = "Address is required")
    String address,

    @NotBlank (message = "City is required")
    String city,

    @NotBlank (message = "Country is required")
    String country,

    String taxNumber,

    String logoPath,

    String bankName,

    String bankAccountName,

    String bankAccountNumber,

    String bankBranch,

    @Pattern(
           regexp = "^[A-Z0-9]{8}(?:[A-Z0-9]{3})?$",
            message = "Invalid SWIFT Code"
    )

    String bankSwiftCode,

    String invoiceTerms,

    Boolean Active
    )  {}
