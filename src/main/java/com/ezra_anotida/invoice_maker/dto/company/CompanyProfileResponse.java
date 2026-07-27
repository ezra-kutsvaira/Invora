package com.ezra_anotida.invoice_maker.dto.company;

public record CompanyProfileResponse(
        Long id,

        String companyName,

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
