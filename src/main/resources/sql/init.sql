INSERT INTO user_service_schema.tenants
(name, code, address, email, created_at)
VALUES('Tenant 1', 'T1','xxx', 'esq@fss.com', '2024-09-18');

INSERT INTO user_service_schema.tenants
(name, code, address, email, created_at)
VALUES('Tenant 2', 'T2','xxx', 'tenant2@fssx.com', '2024-09-18');

-- Insertion de rôles dans la table roles
INSERT INTO user_service_schema.roles (label, description, created_at)
VALUES ('ROLE_ADMIN', 'Administrateur du système', CURRENT_TIMESTAMP),
       ('ROLE_USER', 'Utilisateur standard', CURRENT_TIMESTAMP),
       ('ROLE_MANAGER', 'Responsable du lounge/restaurant', CURRENT_TIMESTAMP),
       ('ROLE_SERVER', 'Serveur dans le lounge/restaurant', CURRENT_TIMESTAMP),
       ('ROLE_COOK', 'Cuisinier dans le lounge/restaurant', CURRENT_TIMESTAMP);
-- Insertion de permissions dans la table permissions
INSERT INTO user_service_schema.permissions (label, description, created_at)
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
       ('VIEW_TENANTS', 'Permission de visualiser les restaurants', CURRENT_TIMESTAMP);

-- Association des rôles et permissions dans la table role_permissions
INSERT INTO user_service_schema.role_permissions (role_id, permission_id)
VALUES
    -- Permissions pour le rôle ADMIN
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'CREATE_ORDER')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_INVENTORY')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'EDIT_INVENTORY')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'DELETE_ORDER')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_REPORTS')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'REJECT_DISH')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'CREATE_DISH')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'COOK_DISH')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'SERVE_DISH')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'REGISTER_PAY_DISH')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_USERS')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'CREATE_USER')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_ADMIN'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_TENANTS')),
    -- Permissions pour le rôle USER
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_USER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_INVENTORY')),


    -- Permissions pour le rôle MANAGER
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_REPORTS')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_INVENTORY')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'EDIT_INVENTORY')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'CREATE_DISH')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'ASSIGN_MENU_CATEGORY')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_USERS')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'CREATE_USER')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_TENANTS')),


    -- Permissions pour le rôle SERVER
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_SERVER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'SERVE_DISH')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_SERVER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'REGISTER_PAY_DISH')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_SERVER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_INVENTORY')),

-- Permissions pour le rôle ROLE_COOK
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_COOK'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'REMOVE_INVENTORY')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_COOK'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_INVENTORY')),
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_COOK'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'COOK_DISH'));
