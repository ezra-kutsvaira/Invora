package dto.customer;

public record CustomerResponse(
        Long id,
        String customerName,
        String email,
        String phone,
        String address,
        String city,
        String country,
        String taxNumber,
        Boolean active
) {
}
