--liquibase formatted sql
--changeset ezra: 001-create initial-invora-schema

CREATE TABLE organizations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_organizations PRIMARY KEY (id),
    CONSTRAINT uk_organizations_slug UNIQUE (slug)
);

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(255),
    created_at DATETIME(6),
    updated_at DATETIME(6)

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE organization_memberships (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_organization_memberships PRIMARY KEY (id),
    CONSTRAINT uk_organization_memberships_organization_user UNIQUE (organization_id, user_id),
    CONSTRAINT fk_memberships_organization FOREIGN KEY (organization_id) REFERENCES organizations (id),
    CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_memberships_organization ON organization_memberships (organization_id);

CREATE INDEX idx_memberships_user ON organization_memberships_user ON organization_memberships (user_id);

CREATE TABLE company_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    company_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    website VARCHAR(255)
    address VARCHAR(2000) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    vat_number VARCHAR(255) NOT NULL,
    tin_number VARCHAR(255) NOT NULL,
    organization_id BIGINT NOT NULL,
    bank_account_name VARCHAR(255),
    bank_account_number VARCHAR(255),
    bank_branch VARCHAR(255),
    bank_swift_code VARCHAR(255),
    invoice_terms VARCHAR(2000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_company_profiles PRIMARY KEY (id),
    CONSTRAINT uk_company_profiles_organization UNIQUE (organization_id),

    CONSTRAINT fk_company_profiles_organization FOREIGN KEY (organization_id) REFERENCES organizations (id)
);

CREATE TABLE currencies (
    
)

