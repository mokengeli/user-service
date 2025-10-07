SET search_path TO user_schema;

INSERT INTO permissions (label, description, created_at)
VALUES ('DELETE_DISH', 'Permission de supprimer des plats', CURRENT_TIMESTAMP),
       ('DELETE_ITEM_INVENTORY', 'Permission de supprimer des items de l''inventaire', CURRENT_TIMESTAMP);

INSERT INTO roles_permissions (role_id, permission_id)
VALUES((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'DELETE_DISH')),
    ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'DELETE_ITEM_INVENTORY'));

--
INSERT INTO permissions (label, description, created_at)
VALUES ('CREATE_CATEGORY_INVENTORY', 'Permission de créer des nouvelles categories d''inventaire', CURRENT_TIMESTAMP),
       ('EDIT_CATEGORY_INVENTORY', 'Permission de modifier une categorie de l''inventaire', CURRENT_TIMESTAMP),
       ('DELETE_CATEGORY_INVENTORY', 'Permission de supprimer des categories d''inventaire', CURRENT_TIMESTAMP);

INSERT INTO roles_permissions (role_id, permission_id)
VALUES((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'CREATE_CATEGORY_INVENTORY')),
      ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'EDIT_CATEGORY_INVENTORY')),
      ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'DELETE_CATEGORY_INVENTORY'));

--
INSERT INTO permissions (label, description, created_at)
VALUES ('VIEW_ALL_CATEGORY_MENU', 'Permission de voir toutes les categories disponibles pour les menus', CURRENT_TIMESTAMP),
       ('DELETE_MENU_CATEGORY', 'Permission de supprimer des categories de menu', CURRENT_TIMESTAMP),
       ('EDIT_MENU_CATEGORY', 'Permission de modifier des categories du menu', CURRENT_TIMESTAMP);

INSERT INTO roles_permissions (role_id, permission_id)
VALUES((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'VIEW_ALL_CATEGORY_MENU')),
      ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'DELETE_MENU_CATEGORY')),
      ((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'EDIT_MENU_CATEGORY')),
      ((SELECT id FROM roles WHERE label = 'ROLE_MANAGER'),
       (SELECT id FROM permissions WHERE label = 'VIEW_ALL_CATEGORY_MENU'));

INSERT INTO roles_permissions (role_id, permission_id)
VALUES((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'ASSIGN_MENU_CATEGORY'));

INSERT INTO roles_permissions (role_id, permission_id)
VALUES((SELECT id FROM roles WHERE label = 'ROLE_ADMIN'),
       (SELECT id FROM permissions WHERE label = 'CREATE_MENU_CATEGORY'));