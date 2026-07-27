package com.ezra_anotida.invoice_maker.dto.company;

import jakarta.validation.constraints.*;

public record UpdateCompanyProfileRequest(

        @Size(max = 150)
        String companyName,

        @Email(message = "Invalid email address")
        String email,

        String phone,

        String website,

        String address,

        String city,

        String country,

        String taxNumber,

        String logoPath,

        String bankName,

        String bankAccountName,

        String bankAccountNumber,

        String bankBranch

) {}
