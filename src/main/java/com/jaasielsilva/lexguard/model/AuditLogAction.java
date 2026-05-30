package com.jaasielsilva.lexguard.model;

public enum AuditLogAction {
    LOGIN,
    LOGOUT,
    CREATE_TITULAR,
    UPDATE_TITULAR,
    DELETE_TITULAR,
    REGISTER_CONSENT,
    REVOKE_CONSENT,
    DATA_ACCESS,
    DATA_EXPORT,
    REQUEST_SUBMITTED,
    REQUEST_HANDLED,
    REPORT_GENERATED
}
