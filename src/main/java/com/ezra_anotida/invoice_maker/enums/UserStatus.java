package com.ezra_anotida.invoice_maker.enums;

public enum UserStatus {

    //The account exists but the user has not verified their email
    PENDING_VERIFICATION,

    //The account is allowed to authenticate and use INVORA
    ACTIVE,

    //The account is temporarily blocked
    LOCKED,

    //The account has been temporarily disabled
    DISABLED
}
