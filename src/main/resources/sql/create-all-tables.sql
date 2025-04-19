CREATE SCHEMA user_service_schema;

-- Table des tenants (tenants)
CREATE TABLE user_service_schema.tenants (
                                             id SERIAL PRIMARY KEY,
                                             code VARCHAR(255) NOT NULL UNIQUE,
                                             name VARCHAR(100) NOT NULL UNIQUE,  -- Nom du client (ex. 'Restaurant A', 'Lounge B')
                                             address TEXT,                       -- Adresse du client
                                             email VARCHAR(100),                 -- Email du client
                                             created_at TIMESTAMP
);

-- Table des utilisateurs (users)
CREATE TABLE user_service_schema.users (
                                           id SERIAL PRIMARY KEY,
                                           tenant_id INT NOT NULL,             -- Identifiant du tenant pour cloisonner les utilisateurs
                                           first_name VARCHAR(100) NOT NULL,   -- Prénom de l'utilisateur
                                           last_name VARCHAR(100) NOT NULL,    -- Nom de famille
                                           post_name VARCHAR(100),             -- Post-nom (utilisé dans les noms congolais)
                                           email VARCHAR(150) UNIQUE, -- Email de l'utilisateur
                                           employee_number VARCHAR(200) NOT NULL UNIQUE, -- Matricule de l'utilisateur
                                           password VARCHAR(255) NOT NULL,
                                           status VARCHAR(20) DEFAULT 'active',-- Statut de l'utilisateur (ex: 'active', 'inactive')
                                           created_at TIMESTAMP ,
                                           updated_at TIMESTAMP
);

-- Table des rôles (roles)
CREATE TABLE user_service_schema.roles (
                                           id SERIAL PRIMARY KEY,
                                           label VARCHAR(50) NOT NULL UNIQUE,  -- Nom du rôle (ex: 'admin', 'serveur')
                                           description TEXT,                       -- Description du rôle
                                           created_at TIMESTAMP
);

-- Table d'association des utilisateurs et des rôles (user_roles)
CREATE TABLE user_service_schema.user_roles (
                                                user_id INT NOT NULL,                  -- Identifiant de l'utilisateur
                                                role_id INT NOT NULL,                  -- Identifiant du rôle
                                                PRIMARY KEY (user_id, role_id)         -- Clé primaire composite
);

-- Table des permissions (permissions)
CREATE TABLE user_service_schema.permissions (
                                                 id SERIAL PRIMARY KEY,
                                                 label VARCHAR(100) NOT NULL UNIQUE,  -- Nom de la permission (ex: 'create_order', 'view_stock')
                                                 description TEXT,                              -- Description de la permission
                                                 created_at TIMESTAMP
);

-- Table d'association des rôles et des permissions (role_permissions)
CREATE TABLE user_service_schema.role_permissions (
                                                      role_id INT NOT NULL,                  -- Identifiant du rôle
                                                      permission_id INT NOT NULL,            -- Identifiant de la permission
                                                      PRIMARY KEY (role_id, permission_id)   -- Clé primaire composite
);

-- Table d'audit (audit_logs)
CREATE TABLE user_service_schema.audit_logs (
                                                id SERIAL PRIMARY KEY,
                                                tenant_id INT NOT NULL,                -- Cloisonnement des logs par tenant
                                                user_id INT NOT NULL,                  -- Utilisateur ayant effectué l'action
                                                action VARCHAR(100) NOT NULL,          -- Action effectuée (ex: 'create', 'update', 'delete')
                                                entity VARCHAR(100),                   -- L'entité affectée par l'action (ex: 'order', 'product')
                                                entity_id INT,                         -- ID de l'entité affectée (optionnel)
                                                description TEXT,                      -- Description de l'action effectuée
                                                created_at TIMESTAMP
);

-- Ajout des contraintes après la création des tables

-- Contrainte de clé étrangère entre users et tenants
ALTER TABLE user_service_schema.users
    ADD CONSTRAINT fk_users_tenants
        FOREIGN KEY (tenant_id) REFERENCES user_service_schema.tenants(id);

-- Contrainte de clé étrangère entre user_roles et users
ALTER TABLE user_service_schema.user_roles
    ADD CONSTRAINT fk_user_roles_users
        FOREIGN KEY (user_id) REFERENCES user_service_schema.users(id);

-- Contrainte de clé étrangère entre user_roles et roles
ALTER TABLE user_service_schema.user_roles
    ADD CONSTRAINT fk_user_roles_roles
        FOREIGN KEY (role_id) REFERENCES user_service_schema.roles(id);

-- Contrainte de clé étrangère entre role_permissions et roles
ALTER TABLE user_service_schema.role_permissions
    ADD CONSTRAINT fk_role_permissions_roles
        FOREIGN KEY (role_id) REFERENCES user_service_schema.roles(id);

-- Contrainte de clé étrangère entre role_permissions et permissions
ALTER TABLE user_service_schema.role_permissions
    ADD CONSTRAINT fk_role_permissions_permissions
        FOREIGN KEY (permission_id) REFERENCES user_service_schema.permissions(id);

-- Contrainte de clé étrangère entre audit_logs et tenants
ALTER TABLE user_service_schema.audit_logs
    ADD CONSTRAINT fk_audit_logs_tenants
        FOREIGN KEY (tenant_id) REFERENCES user_service_schema.tenants(id);

-- Contrainte de clé étrangère entre audit_logs et users
ALTER TABLE user_service_schema.audit_logs
    ADD CONSTRAINT fk_audit_logs_users
        FOREIGN KEY (user_id) REFERENCES user_service_schema.users(id);
