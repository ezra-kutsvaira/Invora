package com.ezra_anotida.invoice_maker.enums;

public enum MembershipStatus {

    //Has organization access
    ACTIVE,

    //Currently invited but has not accepted the organization
    INVITED,

    //The membership is temporarily prevented from accessing the organization
    SUSPENDED,

    //Membership has been removed but is retained for history and auditing
    REMOVED
}
