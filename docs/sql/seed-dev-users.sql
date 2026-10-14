-- =============================================================================
-- Seed de usuarios de PRUEBA para desarrollo local.
-- NO usar en produccion: la contrasena es conocida y esta documentada.
--
-- Uso (con el contenedor de MySQL del TP en marcha y la app ya iniciada al
-- menos una vez, porque las tablas las crea Hibernate con ddl-auto=update):
--   docker exec -i <contenedor-mysql> mysql -uroot -proot iw3_db < docs/sql/seed-dev-users.sql
--
-- Usuario: admin   Contrasena: admin123   Rol: ROLE_ADMIN
--
-- Notas:
--  * El hash BCrypt se genero con GET /demo/encodepass?password=admin123
--    (endpoint de demo de la catedra, clase del 06/10).
--  * account_non_expired, account_non_locked y credentials_non_expired tienen
--    DEFAULT 0 en la base, por eso se fijan en 1 de forma explicita.
--  * Es idempotente: se puede ejecutar mas de una vez (INSERT IGNORE sobre
--    claves unicas).
-- =============================================================================

INSERT IGNORE INTO roles (name, description) VALUES
  ('ROLE_ADMIN', 'Administrador'),
  ('ROLE_USER',  'Usuario');

INSERT IGNORE INTO users
  (username, email, password, enabled,
   account_non_expired, account_non_locked, credentials_non_expired)
VALUES
  ('admin', 'admin@iw3.local',
   '$2a$10$z1SuK4vWtKdIJrp6cvK7q.MJNwmtcy10FsmFcp1/0S2R3iJXOMQCK',
   1, 1, 1, 1);

INSERT IGNORE INTO userroles (id_user, id_role)
SELECT u.id_user, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
