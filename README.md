# IW3 - Proyecto Spring Boot

Proyecto universitario / práctico desarrollado con Java, Spring Boot y MySQL.

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
