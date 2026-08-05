package com.ezra_anotida.invoice_maker.dto.organization;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateOrganizationRequest(
        @Size(min = 1, max = 150, message = "Organization name must contain between 1 and 150 characters")
        String name,

        @Size(min = 1, max = 100, message = "Organization slug must contain between 1 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9-]*$", message = "Slug can only contain letters, numbers and hyphens")
        String slug
) {
}
