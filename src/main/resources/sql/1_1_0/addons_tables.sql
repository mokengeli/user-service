-- ===========================================================================
-- addons_tables.sql — Tables pour le système d'addons premium
-- Version: 1.1.0
-- ===========================================================================

-- Schéma de travail
SET search_path TO user_schema;

-- ===========================================================================
-- 1. Table features - Catalogue des features disponibles
-- ===========================================================================
CREATE TABLE features (
    id               BIGSERIAL PRIMARY KEY,
    code             VARCHAR(50) UNIQUE NOT NULL,
    name             VARCHAR(100) NOT NULL,
    description      TEXT,
    category         VARCHAR(50) NOT NULL,
    is_active        BOOLEAN DEFAULT true NOT NULL,
    display_order    INT,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP WITH TIME ZONE
);

-- Index pour filtrage par catégorie et recherche par code
CREATE INDEX idx_features_category ON features(category);
CREATE INDEX idx_features_active ON features(is_active);
CREATE INDEX idx_features_code ON features(code);

-- ===========================================================================
-- 2. Table tenant_features - Features activées par tenant
-- ===========================================================================
CREATE TABLE tenant_features (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    feature_id          BIGINT NOT NULL REFERENCES features(id) ON DELETE CASCADE,
    enabled             BOOLEAN DEFAULT true NOT NULL,
    activated_at        TIMESTAMP WITH TIME ZONE,
    deactivated_at      TIMESTAMP WITH TIME ZONE,
    activated_by_id     BIGINT REFERENCES users(id) ON DELETE SET NULL,
    deactivated_by_id   BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_tenant_feature UNIQUE(tenant_id, feature_id)
);

-- Index pour optimisation des requêtes /status (très fréquentes)
CREATE INDEX idx_tenant_features_tenant ON tenant_features(tenant_id);
CREATE INDEX idx_tenant_features_enabled ON tenant_features(tenant_id, enabled);
CREATE INDEX idx_tenant_features_feature ON tenant_features(feature_id);

-- ===========================================================================
-- 3. Table addon_requests - Demandes d'activation (Manager → Admin)
-- ===========================================================================
CREATE TABLE addon_requests (
    id                  BIGSERIAL PRIMARY KEY,
    ticket_id           VARCHAR(50) UNIQUE NOT NULL,
    tenant_id           BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    feature_id          BIGINT NOT NULL REFERENCES features(id) ON DELETE CASCADE,
    requested_by_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message             TEXT,
    status              VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    processed_at        TIMESTAMP WITH TIME ZONE,
    processed_by_id     BIGINT REFERENCES users(id) ON DELETE SET NULL
);

-- Index pour filtrage par statut et tenant
CREATE INDEX idx_addon_requests_status ON addon_requests(status);
CREATE INDEX idx_addon_requests_tenant ON addon_requests(tenant_id);
CREATE INDEX idx_addon_requests_ticket ON addon_requests(ticket_id);

-- ===========================================================================
-- 4. Table addon_plans - Plans tarifaires
-- ===========================================================================
CREATE TABLE addon_plans (
    id               BIGSERIAL PRIMARY KEY,
    code             VARCHAR(50) UNIQUE NOT NULL,
    name             VARCHAR(100) NOT NULL,
    description      TEXT,
    price            NUMERIC(10,2) NOT NULL,
    currency         VARCHAR(3) DEFAULT 'USD' NOT NULL,
    billing_period   VARCHAR(20) DEFAULT 'monthly' NOT NULL,
    category         VARCHAR(50) NOT NULL,
    savings          NUMERIC(10,2),
    is_active        BOOLEAN DEFAULT true NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP WITH TIME ZONE
);

-- Index pour filtrage par catégorie, statut actif et recherche par code
CREATE INDEX idx_addon_plans_category ON addon_plans(category);
CREATE INDEX idx_addon_plans_active ON addon_plans(is_active);
CREATE INDEX idx_addon_plans_code ON addon_plans(code);

-- ===========================================================================
-- 5. Table plan_features - Liaison Many-to-Many Plans ↔ Features
-- ===========================================================================
CREATE TABLE plan_features (
    plan_id     BIGINT NOT NULL REFERENCES addon_plans(id) ON DELETE CASCADE,
    feature_id  BIGINT NOT NULL REFERENCES features(id) ON DELETE CASCADE,
    PRIMARY KEY (plan_id, feature_id)
);

-- Index pour recherche par feature
CREATE INDEX idx_plan_features_feature ON plan_features(feature_id);

-- ===========================================================================
-- Commentaires sur les tables
-- ===========================================================================
COMMENT ON TABLE features IS 'Catalogue des features disponibles dans le système';
COMMENT ON TABLE tenant_features IS 'Features activées pour chaque tenant avec traçabilité complète';
COMMENT ON TABLE addon_requests IS 'Demandes d''activation de features par les managers';
COMMENT ON TABLE addon_plans IS 'Catalogue des plans tarifaires disponibles';
COMMENT ON TABLE plan_features IS 'Association entre plans et features incluses';

COMMENT ON COLUMN features.code IS 'Identifiant métier unique (ex: basicCRM, emailMarketing)';
COMMENT ON COLUMN features.name IS 'Nom affichable de la feature (ex: CRM Basique)';
COMMENT ON COLUMN addon_plans.code IS 'Identifiant métier unique du plan (ex: CRM_STARTER, DIGITAL_PRESENCE)';
COMMENT ON COLUMN addon_plans.name IS 'Nom affichable du plan (ex: CRM Starter)';
COMMENT ON COLUMN tenant_features.feature_id IS 'Référence vers la feature (FK sur features.id)';
COMMENT ON COLUMN tenant_features.enabled IS 'État actuel de la feature (true=activée, false=désactivée)';
COMMENT ON COLUMN addon_requests.ticket_id IS 'Identifiant unique format ADDON-YYYY-NNN';
COMMENT ON COLUMN addon_requests.status IS 'Statut: PENDING, APPROVED, REJECTED';
