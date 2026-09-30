# IW3 - Proyecto Spring Boot

Proyecto universitario / práctico desarrollado con Java, Spring Boot y MySQL.
Liquid Gas Terminal API (Backend)

Servicio backend encargado del núcleo transaccional y la lógica de negocio para la administración integral de órdenes de carga de gas líquido. Diseñado bajo arquitectura REST desacoplada y preparado para operar en entornos contenerizados.

### Responsabilidades principales:
* **Integración externa:** Sincronización e ingesta de órdenes de carga desde sistemas externos (SAP) y balanzas de planta (TMS - Terminal Manager System)[cite: 1, 2].
* **Control de acceso y carga:** Generación y validación de contraseñas de activación de 5 dígitos para habilitar el bombeo y entrega de producto según el preset configurado[cite: 2].
* **Telemetría en tiempo real:** Recepción, validación y almacenamiento periódico de variables críticas del caudalímetro másico (masa acumulada, caudal, densidad y temperatura)[cite: 2].
* **Conciliación automática:** Cálculo de diferencias volumétricas/másicas entre balanza y caudalímetro al registrarse el pesaje final (tara vs. pesaje final vs. masa acumulada)[cite: 4].
* **Seguridad y auditoría:** Control de acceso mediante roles, registro de transiciones de estados (1 a 4) y despacho de alertas automáticas por exceso de temperatura[cite: 7, 9].
* **Documentación técnica:** Especificación completa de endpoints con OpenAPI / Swagger[cite: 8].

---

## 🗄️ Base de Datos (Docker)

El proyecto incluye un archivo `docker-compose.yml` para levantar la base de datos MySQL 8.0 en un contenedor de forma rápida.

### Credenciales y Configuración de Conexión
* **Host:** `localhost`
* **Puerto:** `3306`
* **Base de Datos:** `iw3_db`
* **Usuario:** `root`
* **Contraseña:** `root`

---

## 🚀 Comandos de Docker Compose

### 1. Iniciar la Base de Datos
Levanta el contenedor en segundo plano:
```bash
docker compose up -d
```
*(o `docker-compose up -d` en versiones anteriores de Docker)*

### 2. Ver Estado del Contenedor
Verifica si el contenedor `iw3-mysql` está corriendo y saludable:
```bash
docker compose ps
```

### 3. Ver Logs de la Base de Datos
Útil para comprobar si MySQL terminó de inicializar:
```bash
docker compose logs -f mysqldb
```

### 4. Detener el Contenedor
Detiene y remueve el contenedor sin perder los datos guardados:
```bash
docker compose down
```

### 5. Reiniciar o Borrar Datos (Reset completo)
Si necesitas reiniciar la base de datos desde cero (borrando el volumen `mysql_data`):
```bash
docker compose down -v
```

### 6. Acceder a la Consola MySQL dentro del Contenedor
```bash
docker exec -it iw3-mysql mysql -u root -proot iw3_db
```

---

## ☕ Ejecución de la Aplicación Spring Boot

Una vez que la base de datos esté levantada:

```bash
./mvnw spring-boot:run
```

La aplicación se iniciará en `http://localhost:8080`.
