package dto.customer;

public record CustomerSummaryResponse(
        Long id,
        String customerName,
        String email,
        String phone,
        Boolean active
) {
}
