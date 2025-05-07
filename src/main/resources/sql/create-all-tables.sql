-- ===========================================================================
-- create-all-tables.sql — Schéma et tables existantes + extensions SaaS
-- ===========================================================================

-- 1) Schéma principal
CREATE SCHEMA IF NOT EXISTS user_schema;
SET search_path TO user_schema;

-- 2) Tables existantes
-- ------------------------------

-- Table des tenants (tenants)
CREATE TABLE tenants (
                                             id        BIGSERIAL PRIMARY KEY,
                                             code      VARCHAR(255) NOT NULL UNIQUE,
                                             name      VARCHAR(100) NOT NULL UNIQUE,  -- Nom du client (ex. 'Restaurant A', 'Lounge B')
                                             address   TEXT,                          -- Adresse du client
                                             email     VARCHAR(100) NOT NULL UNIQUE,  -- Email du client
                                             created_at TIMESTAMP NOT NULL,
                                             updated_at TIMESTAMP
);

-- Table des utilisateurs (users)
CREATE TABLE users (
                                           id               BIGSERIAL PRIMARY KEY,
                                           tenant_id        BIGINT NOT NULL,            -- Cloisonnement des utilisateurs par tenant
                                           first_name       VARCHAR(100) NOT NULL,
                                           last_name        VARCHAR(100) NOT NULL,
                                           post_name        VARCHAR(100),
                                           email            VARCHAR(150) ,
                                           employee_number  VARCHAR(200) NOT NULL UNIQUE,
                                           password         VARCHAR(255) NOT NULL,
                                           status           VARCHAR(20) DEFAULT 'active',
                                           created_at       TIMESTAMP,
                                           updated_at       TIMESTAMP
);

-- Table des rôles (roles)
CREATE TABLE roles (
                                           id          BIGSERIAL PRIMARY KEY,
                                           label       VARCHAR(50) NOT NULL UNIQUE,
                                           description TEXT,
                                           created_at  TIMESTAMP
);

-- Table d'association des utilisateurs et des rôles (users_roles)
CREATE TABLE users_roles (
                                                user_id BIGINT NOT NULL,
                                                role_id BIGINT NOT NULL,
                                                PRIMARY KEY (user_id, role_id)
);

-- Table des permissions (permissions)
CREATE TABLE permissions (
                                                 id          BIGSERIAL PRIMARY KEY,
                                                 label       VARCHAR(100) NOT NULL UNIQUE,
                                                 description TEXT,
                                                 created_at  TIMESTAMP
);

-- Table d'association des rôles et des permissions (roles_permissions)
CREATE TABLE roles_permissions (
                                                      role_id       BIGINT NOT NULL,
                                                      permission_id BIGINT NOT NULL,
                                                      PRIMARY KEY (role_id, permission_id)
);

-- Table d'audit (audit_logs)
CREATE TABLE audit_logs (
                                                id         BIGSERIAL PRIMARY KEY,
                                                tenant_id  BIGINT NOT NULL,
                                                user_id    BIGINT NOT NULL,
                                                action     VARCHAR(100) NOT NULL,
                                                entity     VARCHAR(100),
                                                entity_id  BIGINT,
                                                description TEXT,
                                                created_at TIMESTAMP
);

-- Séquence pour génération de numéro d'employé par tenant
CREATE TABLE tenant_user_sequence (
                                                          tenant_id  BIGINT PRIMARY KEY,
                                                          last_value BIGINT NOT NULL
);

-- 3) Tables manquantes pour SaaS multi-tenant
-- --------------------------------------------

-- Plans d’abonnement
CREATE TABLE subscription_plans (
                                                       id             BIGSERIAL   PRIMARY KEY,
                                                       code           VARCHAR(50) NOT NULL UNIQUE,
                                                       label          VARCHAR(100) NOT NULL,
                                                       monthly_price  NUMERIC(10,2) NOT NULL,
                                                       features       JSONB        NULL,
                                                       created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
                                                       updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Types d’enseigne
CREATE TABLE establishment_types (
                                                        id          BIGSERIAL   PRIMARY KEY,
                                                        code        VARCHAR(50) NOT NULL UNIQUE,
                                                        label       VARCHAR(100) NOT NULL,
                                                        description TEXT        NULL,
                                                        created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
                                                        updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- Historique des abonnements (subscription_histories)
CREATE TABLE subscription_histories (
                                                          id                   BIGSERIAL PRIMARY KEY,
                                                          tenant_id            BIGINT    NOT NULL,
                                                          subscription_plan_id BIGINT    NOT NULL,
                                                          start_date           DATE      NOT NULL,
                                                          end_date             DATE      NULL,
                                                          created_at           TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 4) Mises à jour des tables existantes
-- --------------------------------------

-- Ajout des colonnes et contraintes FK sur tenants
ALTER TABLE tenants
    ADD COLUMN establishment_type_id BIGINT NOT NULL,
  ADD COLUMN subscription_plan_id  BIGINT NOT NULL;

ALTER TABLE tenants
    ADD CONSTRAINT fk_tenants_estab_type
        FOREIGN KEY (establishment_type_id)
            REFERENCES establishment_types(id),
  ADD CONSTRAINT fk_tenants_sub_plan
    FOREIGN KEY (subscription_plan_id)
    REFERENCES subscription_plans(id);

-- Contraintes FK pour subscription_histories
ALTER TABLE subscription_histories
    ADD CONSTRAINT fk_subhist_tenant
        FOREIGN KEY (tenant_id)
            REFERENCES tenants(id),
  ADD CONSTRAINT fk_subhist_plan
    FOREIGN KEY (subscription_plan_id)
    REFERENCES subscription_plans(id);

-- 5) Contraintes FK existantes pour sécurité et audit
-- ----------------------------------------------------

ALTER TABLE users
    ADD CONSTRAINT fk_users_tenants
        FOREIGN KEY (tenant_id)
            REFERENCES tenants(id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_users_roles_users
        FOREIGN KEY (user_id)
            REFERENCES users(id),
  ADD CONSTRAINT fk_users_roles_roles
    FOREIGN KEY (role_id)
    REFERENCES roles(id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_role_perms_roles
        FOREIGN KEY (role_id)
            REFERENCES roles(id),
  ADD CONSTRAINT fk_role_perms_perms
    FOREIGN KEY (permission_id)
    REFERENCES permissions(id);

ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_logs_tenants
        FOREIGN KEY (tenant_id)
            REFERENCES tenants(id),
  ADD CONSTRAINT fk_audit_logs_users
    FOREIGN KEY (user_id)
    REFERENCES users(id);

-- 6) Indexes
-- ----------

-- Indexes sur tenants pour nouveaux FK
CREATE INDEX idx_tenants_estab_type_id
    ON tenants(establishment_type_id);

CREATE INDEX idx_tenants_sub_plan_id
    ON tenants(subscription_plan_id);

-- Indexes pour subscription_histories
CREATE INDEX idx_subhist_tenant_id
    ON subscription_histories(tenant_id);

CREATE INDEX idx_subhist_plan_id
    ON subscription_histories(subscription_plan_id);

-- Supplémentaires demandées
CREATE INDEX idx_sub_hist_plan_id
    ON subscription_histories(subscription_plan_id);

-- Indexes sur codes de référence
CREATE INDEX idx_estab_type_code
    ON establishment_types(code);

CREATE INDEX idx_sub_plan_code
    ON subscription_plans(code);

-- Indexes existants pour sécurité/audit
CREATE INDEX idx_users_tenant_id
    ON users(tenant_id);

CREATE INDEX idx_users_roles_user_id
    ON users_roles(user_id);

CREATE INDEX idx_users_roles_role_id
    ON users_roles(role_id);

CREATE INDEX idx_roles_permissions_role_id
    ON roles_permissions(role_id);

CREATE INDEX idx_roles_permissions_permission_id
    ON roles_permissions(permission_id);

CREATE INDEX idx_audit_logs_tenant_id
    ON audit_logs(tenant_id);

CREATE INDEX idx_audit_logs_user_id
    ON audit_logs(user_id);
