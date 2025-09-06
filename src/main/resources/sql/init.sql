-- init.sql — Initialisation des données de référence avec SaaS multi-tenant

-- 1) Schéma
SET search_path TO user_schema;


-- 2) Plans d’abonnement initiaux
INSERT INTO subscription_plans
    (code, label, monthly_price, features, created_at, updated_at)
VALUES ('STANDARD', 'Standard', 29.99, '{}'::jsonb, NOW(), NOW()),
       ('PREMIUM', 'Premium', 59.99, '{}'::jsonb, NOW(), NOW()),
       ('GOLD', 'Premium', 99.99, '{}'::jsonb, NOW(), NOW());

-- 3) Types d’enseigne initiaux
INSERT INTO establishment_types
    (code, label, description, created_at, updated_at)
VALUES ('RESTAURANT', 'Restaurant', '', NOW(), NOW()),
       ('BAR', 'Bar', 'Bar', NOW(), NOW()),
       ('LOUNGE', 'Lounge', 'Lounge', NOW(), NOW()),
       ('PLATFORM', 'Platform', 'Tenant interne SaaS', NOW(), NOW());

-- 4) Tenant “Platform” et ses FKs vers plan & type
INSERT INTO tenants
(name, code, address, email, establishment_type_id, subscription_plan_id, created_at)
VALUES ('Mokengeli Biloko Plateform',
        'mok-bil',
        'xxx',
        'bacos.systemes@gmail.com',
        (SELECT id FROM establishment_types WHERE code = 'PLATFORM'),
        (SELECT id FROM subscription_plans WHERE code = 'PREMIUM'),
        NOW());

-- 5) Historique de souscription pour “administrateur”
INSERT INTO subscription_histories
    (tenant_id, subscription_plan_id, start_date, created_at)
VALUES ((SELECT id FROM tenants WHERE code = 'mok-bil'),
        (SELECT id FROM subscription_plans WHERE code = 'PREMIUM'),
        NOW(),
        NOW());

-- 6) Initialisation de la séquence de numéros d’employé
INSERT INTO tenant_user_sequence
    (tenant_id, last_value)
VALUES ((SELECT id FROM tenants WHERE code = 'mok-bil'),
        0);

-- 7) Création de l’utilisateur administrateur “Emmanuel B”

-- password: 123
INSERT INTO users
(tenant_id,
 first_name,
 last_name,
 post_name,
 user_name,
 email,
 employee_number,
 password,
 status,
 created_at,
 updated_at)
VALUES ((SELECT id FROM tenants WHERE code = 'mok-bil'),
        'Administrateur',
        'Admin',
        NULL,
        'admin',
        NULL,
        'admin-plateform',
        '$2a$10$zgdZgS9BAYe7B0NrUZkg8eUItX.ik54oR7jaM23iVboM3gbnT4D0C',
        'ACTIVE',
        NOW(),
        NULL);


-- Insertion de rôles dans la table roles
INSERT INTO roles (label, description, created_at)
VALUES ('ROLE_ADMIN', 'Administrateur du système', CURRENT_TIMESTAMP),
       ('ROLE_USER', 'Utilisateur standard', CURRENT_TIMESTAMP),
       ('ROLE_MANAGER', 'Responsable du lounge/restaurant', CURRENT_TIMESTAMP),
       ('ROLE_SERVER', 'Serveur dans le lounge/restaurant', CURRENT_TIMESTAMP),
       ('ROLE_COOK', 'Cuisinier dans le lounge/restaurant', CURRENT_TIMESTAMP);
-- Insertion de permissions dans la table permissions
INSERT INTO permissions (label, description, created_at)
VALUES ('CREATE_ORDER', 'Permission de créer des commandes', CURRENT_TIMESTAMP),
       ('VIEW_INVENTORY', 'Permission de visualiser les stocks', CURRENT_TIMESTAMP),
       ('EDIT_INVENTORY', 'Permission de modifier le stock', CURRENT_TIMESTAMP),
       ('ADD_INVENTORY', 'Permission d''ajouter  des elements dans le stock', CURRENT_TIMESTAMP),
       ('REMOVE_INVENTORY', 'Permission de retirer des elements dans le stock', CURRENT_TIMESTAMP),
       ('DELETE_ORDER', 'Permission de supprimer des commandes', CURRENT_TIMESTAMP),
       ('VIEW_REPORTS', 'Permission de visualiser les rapports', CURRENT_TIMESTAMP),
       ('REJECT_DISH', 'Permission de rejeter un plat', CURRENT_TIMESTAMP),
       ('COOK_DISH', 'Permission de preparer', CURRENT_TIMESTAMP),
       ('CREATE_DISH', 'Permission de creer des plats', CURRENT_TIMESTAMP),
       ('SERVE_DISH', 'Permission de servir', CURRENT_TIMESTAMP),
       ('REGISTER_PAY_DISH', 'Permission d''enregistrer un paiement', CURRENT_TIMESTAMP),
       ('CREATE_MENU_CATEGORY', 'Permission de creer une category pour le menu', CURRENT_TIMESTAMP),
       ('ASSIGN_MENU_CATEGORY', 'Permission d''ajouter une category du menu à un tenant. ', CURRENT_TIMESTAMP),
       ('CREATE_USER', 'Permission de créer des utilisateurs', CURRENT_TIMESTAMP),
       ('VIEW_USERS', 'Permission de visualiser les utilisateurs', CURRENT_TIMESTAMP),
       ('VIEW_TENANTS', 'Permission de visualiser les restaurants', CURRENT_TIMESTAMP),
       ('CREATE_TENANTS', 'Permission de créer les restaurants', CURRENT_TIMESTAMP);

-- Association des rôles et permissions dans la table roles_permissions
INSERT INTO roles_permissions (role_id, permission_id)
VALUES
    -- Permissions pour le rôle ADMIN
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'CREATE_ORDER')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'VIEW_INVENTORY')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'EDIT_INVENTORY')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'DELETE_ORDER')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'VIEW_REPORTS')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'REJECT_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'CREATE_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'COOK_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'SERVE_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'REGISTER_PAY_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'VIEW_USERS')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'VIEW_TENANTS')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM permissions WHERE label = 'CREATE_TENANTS')),
    -- Permissions pour le rôle USER
    ((SELECT id FROM roles WHERE label = 'ROLE_USER'),
     (SELECT id FROM permissions WHERE label = 'VIEW_INVENTORY')),


    -- Permissions pour le rôle MANAGER
    ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM permissions WHERE label = 'VIEW_REPORTS')),
    ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM permissions WHERE label = 'VIEW_INVENTORY')),
    ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM permissions WHERE label = 'EDIT_INVENTORY')),
    ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM permissions WHERE label = 'CREATE_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM permissions WHERE label = 'ASSIGN_MENU_CATEGORY')),
    ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM permissions WHERE label = 'VIEW_USERS')),
    ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM permissions WHERE label = 'CREATE_USER')),
    ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM permissions WHERE label = 'VIEW_TENANTS')),


    -- Permissions pour le rôle SERVER
    ((SELECT id FROM roles WHERE label = 'ROLE_SERVER'),
     (SELECT id FROM permissions WHERE label = 'SERVE_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_SERVER'),
     (SELECT id FROM permissions WHERE label = 'REGISTER_PAY_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_SERVER'),
     (SELECT id FROM permissions WHERE label = 'VIEW_INVENTORY')),

-- Permissions pour le rôle ROLE_COOK
    ((SELECT id FROM roles WHERE label = 'ROLE_COOK'),
     (SELECT id FROM permissions WHERE label = 'REMOVE_INVENTORY')),
    ((SELECT id FROM roles WHERE label = 'ROLE_COOK'),
     (SELECT id FROM permissions WHERE label = 'VIEW_INVENTORY')),
    ((SELECT id FROM roles WHERE label = 'ROLE_COOK'),
     (SELECT id FROM permissions WHERE label = 'COOK_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_COOK'),
     (SELECT id FROM permissions WHERE label = 'REJECT_DISH'));


INSERT INTO users_roles (role_id, user_id)
VALUES ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
        (SELECT id FROM users WHERE employee_number = 'admin-plateform'));


INSERT INTO session_limits(app_type, max_sessions)
VALUES ('WEB', 4),
       ('PHONE', 1);

-- Ajout supplementaire 30/07/2025
INSERT INTO permissions (label, description, created_at)
VALUES ('ORDER_DEBT_VALIDATION', 'Permission de valider les dettes sur les commandes  ', CURRENT_TIMESTAMP),
       ('CLOSE_ORDER_WITH_DEBT', 'Permission de cloturer une commande avec impayé  ', CURRENT_TIMESTAMP);


INSERT INTO roles_permissions (role_id, permission_id)
VALUES  ((SELECT id FROM roles WHERE label = 'ROLE_SERVER'),
         (SELECT id FROM permissions WHERE label = 'CLOSE_ORDER_WITH_DEBT')),
        ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
         (SELECT id FROM permissions WHERE label = 'CLOSE_ORDER_WITH_DEBT')),
        ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
         (SELECT id FROM permissions WHERE label = 'ORDER_DEBT_VALIDATION')),
        ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
         (SELECT id FROM permissions WHERE label = 'COOK_DISH')),
        ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
         (SELECT id FROM permissions WHERE label = 'SERVE_DISH')),
        ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
         (SELECT id FROM permissions WHERE label = 'REJECT_DISH')),
        ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
         (SELECT id FROM permissions WHERE label = 'REGISTER_PAY_DISH'));
