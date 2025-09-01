SET search_path TO user_schema;

INSERT INTO roles (label, description, created_at)
VALUES ('ROLE_CASHIER', 'Caissier dans le lounge/restaurant', CURRENT_TIMESTAMP);
INSERT INTO roles_permissions (role_id, permission_id)
VALUES((SELECT id FROM roles WHERE label = 'ROLE_CASHIER'),
     (SELECT id FROM permissions WHERE label = 'REGISTER_PAY_DISH'));
INSERT INTO roles_permissions (role_id, permission_id)
VALUES((SELECT id FROM roles WHERE label = 'ROLE_CASHIER'),
       (SELECT id FROM permissions WHERE label = 'CLOSE_ORDER_WITH_DEBT'));