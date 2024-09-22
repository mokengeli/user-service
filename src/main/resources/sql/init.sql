INSERT INTO user_service_schema.tenants
("name", address, email, created_at)
VALUES('Tenant 1', 'xxx', 'esq@fss.com', '2024-09-18');

-- Insertion de rôles dans la table roles
INSERT INTO user_service_schema.roles (label, description, created_at)
VALUES ('ROLE_ADMIN', 'Administrateur du système', CURRENT_TIMESTAMP),
       ('ROLE_USER', 'Utilisateur standard', CURRENT_TIMESTAMP),
       ('ROLE_MANAGER', 'Responsable du lounge/restaurant', CURRENT_TIMESTAMP),
       ('ROLE_SERVER', 'Serveur dans le lounge/restaurant', CURRENT_TIMESTAMP);
-- Insertion de permissions dans la table permissions
INSERT INTO user_service_schema.permissions (label, description, created_at)
VALUES ('CREATE_ORDER', 'Permission de créer des commandes', CURRENT_TIMESTAMP),
       ('VIEW_INVENTORY', 'Permission de visualiser les stocks', CURRENT_TIMESTAMP),
       ('EDIT_INVENTORY', 'Permission de modifier les stocks', CURRENT_TIMESTAMP),
       ('DELETE_ORDER', 'Permission de supprimer des commandes', CURRENT_TIMESTAMP),
       ('VIEW_REPORTS', 'Permission de visualiser les rapports', CURRENT_TIMESTAMP);
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

    -- Permissions pour le rôle USER
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_USER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_INVENTORY')),

    -- Permissions pour le rôle MANAGER
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_MANAGER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'VIEW_REPORTS')),

    -- Permissions pour le rôle SERVER
    ((SELECT id FROM user_service_schema.roles WHERE label = 'ROLE_SERVER'),
     (SELECT id FROM user_service_schema.permissions WHERE label = 'CREATE_ORDER'));
