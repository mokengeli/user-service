INSERT INTO user_service_schema.tenants
("name", address, email, created_at)
VALUES('Tenant 1', 'xxx', 'esq@fss.com', '2024-09-18');


-- Insertion des rôles de base dans le schéma user_service_schema
INSERT INTO user_service_schema.roles (role_name, description, created_at) VALUES
                                                                               ('Administrator', 'Gère l''ensemble des fonctionnalités du système', NOW()),
                                                                               ('Waiter', 'Gère les commandes des clients', NOW()),
                                                                               ('Manager', 'Supervise les serveurs et gère l''inventaire', NOW()),
                                                                               ('Customer', 'Utilisateur ayant accès aux fonctionnalités client', NOW()),
                                                                               ('Supervisor', 'Supervise les managers et les opérations globales', NOW());

-- Insertion des permissions de base dans le schéma user_service_schema
INSERT INTO user_service_schema.permissions (permission_name, description, created_at) VALUES
                                                                                           ('create_user', 'Autorisation de créer un utilisateur', NOW()),
                                                                                           ('update_user', 'Autorisation de modifier un utilisateur', NOW()),
                                                                                           ('delete_user', 'Autorisation de supprimer un utilisateur', NOW()),
                                                                                           ('view_user', 'Autorisation de voir les informations d''un utilisateur', NOW()),
                                                                                           ('create_order', 'Autorisation de créer une commande', NOW()),
                                                                                           ('view_order', 'Autorisation de voir une commande', NOW()),
                                                                                           ('update_order', 'Autorisation de modifier une commande', NOW()),
                                                                                           ('delete_order', 'Autorisation de supprimer une commande', NOW()),
                                                                                           ('manage_inventory', 'Autorisation de gérer l''inventaire', NOW());


-- Administrator : Toutes les permissions
INSERT INTO user_service_schema.role_permissions (role_id, permission_id, assigned_at)
SELECT r.id, p.id, NOW()
FROM user_service_schema.roles r, user_service_schema.permissions p
WHERE r.role_name = 'Administrator';

-- Waiter : Créer et voir des commandes
INSERT INTO user_service_schema.role_permissions (role_id, permission_id, assigned_at)
SELECT r.id, p.id, NOW()
FROM user_service_schema.roles r, user_service_schema.permissions p
WHERE r.role_name = 'Waiter'
  AND p.permission_name IN ('create_order', 'view_order');

-- Manager : Gérer l'inventaire, voir et gérer les commandes
INSERT INTO user_service_schema.role_permissions (role_id, permission_id, assigned_at)
SELECT r.id, p.id, NOW()
FROM user_service_schema.roles r, user_service_schema.permissions p
WHERE r.role_name = 'Manager'
  AND p.permission_name IN ('create_order', 'view_order', 'update_order', 'manage_inventory');

-- Supervisor : Voir et gérer les utilisateurs et les commandes
INSERT INTO user_service_schema.role_permissions (role_id, permission_id, assigned_at)
SELECT r.id, p.id, NOW()
FROM user_service_schema.roles r, user_service_schema.permissions p
WHERE r.role_name = 'Supervisor'
  AND p.permission_name IN ('create_user', 'update_user', 'view_user', 'create_order', 'view_order');

-- Customer : Accéder aux fonctionnalités de commande
INSERT INTO user_service_schema.role_permissions (role_id, permission_id, assigned_at)
SELECT r.id, p.id, NOW()
FROM user_service_schema.roles r, user_service_schema.permissions p
WHERE r.role_name = 'Customer'
  AND p.permission_name IN ('create_order', 'view_order');
