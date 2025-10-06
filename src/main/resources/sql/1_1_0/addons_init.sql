-- ===========================================================================
-- addons_init.sql — Données de référence pour le système d'addons (MVP)
-- Version: 1.1.0
-- ===========================================================================

-- Schéma de travail
SET search_path TO user_schema;

-- ===========================================================================
-- 1. Insertion des features MVP (4 fonctionnalités essentielles)
-- ===========================================================================

INSERT INTO features (code, name, description, category, display_order, is_active)
VALUES
    -- CRM Features (2)
    ('basicCRM', 'CRM Basique', 'Gestion des clients, contacts et historique', 'crm', 1, true),
    ('emailMarketing', 'Email Marketing', 'Campagnes d''emailing et newsletters', 'crm', 2, true),

    -- Reservations & Ordering Features (2)
    ('onlineBooking', 'Réservation en ligne', 'Widget de réservation pour site web', 'reservations', 3, true),
    ('onlineMenu', 'Menu en ligne', 'Catalogue de produits consultable en ligne', 'onlineOrdering', 4, true);

-- ===========================================================================
-- 2. Plans tarifaires MVP (3 plans simples)
-- ===========================================================================

INSERT INTO addon_plans (code, name, description, price, currency, billing_period, category, savings, is_active)
VALUES
    ('CRM_STARTER', 'CRM Starter', 'Gestion client de base pour démarrer', 29.00, 'USD', 'monthly', 'crm', NULL, true),
    ('CRM_PRO', 'CRM Pro', 'CRM complet avec marketing email', 59.00, 'USD', 'monthly', 'crm', NULL, true),
    ('DIGITAL_PRESENCE', 'Présence Digitale', 'Réservation et menu en ligne pour VOTRE visibilité', 49.00, 'USD', 'monthly', 'all', NULL, true);

-- ===========================================================================
-- 3. Association Plans ↔ Features (via BIGINT id)
-- ===========================================================================

-- CRM Starter: basicCRM uniquement
INSERT INTO plan_features (plan_id, feature_id)
SELECT
    (SELECT id FROM addon_plans WHERE code = 'CRM_STARTER'),
    id
FROM features
WHERE code = 'basicCRM';

-- CRM Pro: basicCRM + emailMarketing
INSERT INTO plan_features (plan_id, feature_id)
SELECT
    (SELECT id FROM addon_plans WHERE code = 'CRM_PRO'),
    id
FROM features
WHERE code IN ('basicCRM', 'emailMarketing');

-- Digital Presence: onlineBooking + onlineMenu
INSERT INTO plan_features (plan_id, feature_id)
SELECT
    (SELECT id FROM addon_plans WHERE code = 'DIGITAL_PRESENCE'),
    id
FROM features
WHERE code IN ('onlineBooking', 'onlineMenu');

-- ===========================================================================
-- 4. Permission pour gérer les addons
-- ===========================================================================

-- Insertion de la permission
INSERT INTO permissions (label, description, created_at)
VALUES ('MANAGE_TENANT_ADDONS', 'Permission de gérer les addons des tenants', CURRENT_TIMESTAMP)
ON CONFLICT (label) DO NOTHING;

-- Association avec le rôle ROLE_ADMIN
INSERT INTO roles_permissions (role_id, permission_id)
SELECT
    (SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
    (SELECT id FROM permissions WHERE label = 'MANAGE_TENANT_ADDONS')
WHERE NOT EXISTS (
    SELECT 1 FROM roles_permissions
    WHERE role_id = (SELECT id FROM roles WHERE label = 'ROLE_ADMIN')
    AND permission_id = (SELECT id FROM permissions WHERE label = 'MANAGE_TENANT_ADDONS')
);

-- ===========================================================================
-- 5. Données de test pour le tenant platform (optionnel)
-- ===========================================================================

-- Activer basicCRM pour le tenant mok-bil
INSERT INTO tenant_features (tenant_id, feature_id, enabled, activated_at, activated_by_id, created_at)
SELECT
    (SELECT id FROM tenants WHERE code = 'mok-bil'),
    (SELECT id FROM features WHERE code = 'basicCRM'),
    true,
    NOW(),
    (SELECT id FROM users WHERE employee_number = 'admin-plateform'),
    NOW()
WHERE EXISTS (SELECT 1 FROM tenants WHERE code = 'mok-bil')
  AND EXISTS (SELECT 1 FROM features WHERE code = 'basicCRM')
ON CONFLICT (tenant_id, feature_id) DO NOTHING;
