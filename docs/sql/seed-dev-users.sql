-- =============================================================================
-- Seed de usuarios de PRUEBA para desarrollo local.
-- NO usar en produccion: la contrasena es conocida y esta documentada.
--
-- Uso (con el contenedor de MySQL del TP en marcha y la app ya iniciada al
-- menos una vez, porque las tablas las crea Hibernate con ddl-auto=update):
--   docker exec -i <contenedor-mysql> mysql -uroot -proot iw3_db < docs/sql/seed-dev-users.sql
--
-- Usuarios (contrasena = <usuario>123, solo desarrollo) y su rol:
--   admin       ROLE_ADMIN       todo
--   supervisor  ROLE_SUPERVISOR  consulta, cierra ordenes, acepta alarmas
--   operador    ROLE_OPERADOR    consulta y cierra ordenes
--   sap         ROLE_SAP         sistema externo: recibe ordenes (Punto 1)
--   tms         ROLE_TMS         sistema externo: balanza (Puntos 2 y 5)
--   loader      ROLE_LOADER      sistema externo: sistema de carga (Puntos 3 y 4)
-- La matriz completa de roles por endpoint esta en docs/final-fase-1-roles.md.
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
  ('ROLE_ADMIN',      'Administrador'),
  ('ROLE_USER',       'Usuario'),
  ('ROLE_SUPERVISOR', 'Supervisor de planta'),
  ('ROLE_OPERADOR',   'Operador de planta'),
  ('ROLE_SAP',        'Sistema externo SAP'),
  ('ROLE_TMS',        'Sistema externo de balanza (TMS)'),
  ('ROLE_LOADER',     'Sistema externo de carga');

INSERT IGNORE INTO users
  (username, email, password, enabled,
   account_non_expired, account_non_locked, credentials_non_expired)
VALUES
  ('admin', 'admin@iw3.local',
   '$2a$10$z1SuK4vWtKdIJrp6cvK7q.MJNwmtcy10FsmFcp1/0S2R3iJXOMQCK',
   1, 1, 1, 1);

INSERT IGNORE INTO users
  (username, email, password, enabled,
   account_non_expired, account_non_locked, credentials_non_expired)
VALUES
  ('supervisor', 'supervisor@iw3.local',
   '$2a$10$I0FKbeeNHf67Z.2lpA1nLOtGRvhMqK/Y8zSfp9fiO1oKIXPSlX9ji',
   1, 1, 1, 1),
  ('operador', 'operador@iw3.local',
   '$2a$10$T1ukcGRIV/ZTZN/RYOJHy.HTJ0fI5zKO7sVeptZiT5Zhu.vagTBYa',
   1, 1, 1, 1),
  ('sap', 'sap@iw3.local',
   '$2a$10$qTc/U00VMynAsIw14oGBZuOUbIskNHyclQVDLjwYsCkKGRgfwDny2',
   1, 1, 1, 1),
  ('tms', 'tms@iw3.local',
   '$2a$10$0SpHXtxDLofiBBnzUVOrO.y7bES1sM8RuY5ywu43C1wp03tE5YUHu',
   1, 1, 1, 1),
  ('loader', 'loader@iw3.local',
   '$2a$10$01Pnfvtf3vcAEeNrZwduDelgp40uGiyCgOrR73W8DIH1tgMkscb7.',
   1, 1, 1, 1);

INSERT IGNORE INTO userroles (id_user, id_role)
SELECT u.id_user, r.id
FROM users u, roles r
WHERE (u.username = 'admin'      AND r.name = 'ROLE_ADMIN')
   OR (u.username = 'supervisor' AND r.name = 'ROLE_SUPERVISOR')
   OR (u.username = 'operador'   AND r.name = 'ROLE_OPERADOR')
   OR (u.username = 'sap'        AND r.name = 'ROLE_SAP')
   OR (u.username = 'tms'        AND r.name = 'ROLE_TMS')
   OR (u.username = 'loader'     AND r.name = 'ROLE_LOADER');
