package dto.customer;

import jakarta.validation.constraints.*;

public record UpdateCustomerRequest(
        @Size(max = 255)
        String customerName,

        @Email(message = "Invalid email address")
        String email,

        String phone,
        String address,
        String city,
        String country,
        String taxNumber,
        Boolean active) {
}
