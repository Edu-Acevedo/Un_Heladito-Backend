# Un Heladito API

Backend REST de la tienda online Un Heladito, desarrollado con Spring Boot y organizado por capas.

## Requisitos

- JDK 21 instalado.
- IntelliJ IDEA (o editor compatible con proyectos Maven).
- MySQL Server instalado y en ejecución.
- MySQL Workbench (opcional, recomendado para administrar la base de datos).
- Conexión a internet la primera vez que Maven descargue las dependencias.

Comprueba Java desde una terminal:

```powershell
java -version
```

Debe mostrar Java 21. Si tu equipo muestra otra versión, revisaremos el ajuste antes de compilar.

## Preparar MySQL

1. Abre MySQL Workbench y conéctate al servidor local.
2. Abre y ejecuta `database/01_create_database.sql`.
3. Se creará la base `un_heladito`.

## Configurar credenciales

La API lee estas variables de entorno:

- `DB_URL`: URL JDBC de MySQL.
- `DB_USERNAME`: usuario de MySQL (por defecto `root`).
- `DB_PASSWORD`: contraseña del usuario (por defecto vacía).

En IntelliJ, abre **Run → Edit Configurations...**, selecciona la configuración de `UnHeladitoApiApplication` y agrega las variables en **Environment variables**. Ejemplo:

```text
DB_URL=jdbc:mysql://localhost:3306/un_heladito?serverTimezone=America/Santiago&allowPublicKeyRetrieval=true&useSSL=false;DB_USERNAME=root;DB_PASSWORD=TU_CLAVE
```

Reemplaza `TU_CLAVE` por la contraseña que configuraste en MySQL. No la escribas en archivos que subirás a GitHub.

## Ejecutar desde IntelliJ

1. Abre la carpeta que contiene este `pom.xml`.
2. Acepta la importación del proyecto Maven y espera la descarga de dependencias.
3. Abre `src/main/java/cl/duoc/unheladito/UnHeladitoApiApplication.java`.
4. Presiona el botón verde **Run** junto a la clase.
5. Comprueba en la consola que Tomcat inició en el puerto 8080.

También puedes ejecutar desde una terminal que tenga Maven instalado:

```bash
mvn spring-boot:run
```

## Comprobaciones iniciales

- Estado de la API: `http://localhost:8080/api/estado`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

El endpoint `/api/estado` ejecuta `SELECT 1` para comprobar tanto la respuesta del servidor como el acceso a MySQL. Devuelve HTTP 200 cuando ambos están operativos y HTTP 503 si la consulta a MySQL falla.

## Estructura

```text
src/main/java/cl/duoc/unheladito/
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
├── security/
├── service/
└── UnHeladitoApiApplication.java
```

## Alcance de esta primera etapa

Esta entrega crea el proyecto, configura el acceso a MySQL, habilita CORS para Vite durante el desarrollo, incorpora Swagger y agrega un endpoint de estado. Los modelos y operaciones CRUD se desarrollarán en etapas posteriores.
