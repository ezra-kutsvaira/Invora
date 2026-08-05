package com.ezra_anotida.invoice_maker.dto.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateOrganizationRequest(

        @NotBlank(message = "Organization name is required")
        @Size(max = 150, message = "Organization name cannot exceed 150 characters")
        String name,

        @Size(max = 100, message = "Organization slug cannot exceed 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9-]*$", message = "Slug can only contain letters, numbers and hyphens")
        String slug
) {
}