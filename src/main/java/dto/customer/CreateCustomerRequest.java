package dto.customer;

import jakarta.validation.constraints.*;

public record CreateCustomerRequest(
        @NotBlank(message = "Customer name is required")
        @Size(max = 255)
        String customerName,

        @Email(message = "Invalid email address")
        String email,

        String phone,
        String address,
        String city,
        String country,
        String taxNumber
        )
{}
