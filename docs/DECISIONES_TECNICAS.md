# Decisiones técnicas iniciales — Un Heladito API

## Stack aprobado para la primera etapa

| Elemento | Decisión | Motivo |
|---|---|---|
| Lenguaje | Java 21 | Versión LTS y adecuada para el entorno Spring Boot elegido. |
| Framework | Spring Boot 4.1.3 | Framework exigido por la evaluación y alineado con los tutoriales del curso. |
| Construcción | Maven | Es el sistema utilizado en los tutoriales del profesor. |
| Persistencia | Spring Data JPA | Permite mapear entidades Java y trabajar con repositorios. |
| ORM | Hibernate, gestionado por Spring Data JPA | Implementa la persistencia de entidades. |
| Base de datos | MySQL | Mantiene productos, categorías, usuarios y pedidos después de reiniciar la API. |
| API | Spring MVC | Expone endpoints REST que intercambian JSON. |
| Validación | Jakarta Bean Validation | Valida los datos recibidos por los endpoints. |
| Documentación | springdoc-openapi 3.2.0 | Genera OpenAPI y Swagger UI; la matriz oficial lo declara compatible con Spring Boot 4.1.x. |
| Lombok | No se incorpora por ahora | Se mantiene el estilo explícito de constructores, getters y setters de los tutoriales para facilitar el aprendizaje y la defensa. |

## Arquitectura por capas

- `controller`: recibe las solicitudes HTTP y construye las respuestas.
- `service`: contiene las reglas de negocio.
- `repository`: consulta y persiste datos mediante Spring Data JPA.
- `entity`: representa las entidades que se almacenan en MySQL.
- `dto`: define los datos de entrada y salida de la API.
- `exception`: contiene excepciones y su manejo centralizado.
- `config`: configura componentes generales como CORS y OpenAPI.
- `security`: se incorporará al implementar Spring Security y JWT.

## Criterio de migración

Los datos centrales y las reglas de negocio migrarán al backend. React conservará presentación, navegación, estado visual y datos temporales de interacción. El backend será la fuente de verdad para precios, stock, usuarios, permisos, pedidos y totales de compra.

## Nota sobre configuración

Las credenciales se leen desde variables de entorno (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`). No se deben guardar contraseñas reales en GitHub.
