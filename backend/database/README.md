# Database & Local Development

## Start a local MySQL instance with Docker

1. Copy `.env.example` to `.env` and edit credentials if needed.
2. Start DB and Adminer (web UI) with:

   docker compose up -d

3. Adminer will be available at http://localhost:8081 (use database/credentials from .env).

## Migrations

- Flyway migrations are placed in `src/main/resources/db/migration` and are executed automatically on application startup when a JDBC datasource is available and `spring.flyway.enabled` is true.

- To run migrations manually with Maven:

  mvn -Dflyway.url=jdbc:mysql://localhost:3306/medicine_sales -Dflyway.user=root -Dflyway.password=secret org.flywaydb:flyway-maven-plugin:migrate

(Or configure the plugin in your IDE with the correct URL/credentials.)

## Running the application locally

- Use the `dev` profile to load `application-dev.properties`:

  # Windows PowerShell
  $env:SPRING_PROFILES_ACTIVE = "dev"; .\mvnw spring-boot:run

  # Unix
  SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

- The app will connect to the DB, run Flyway migrations and start.

## Notes
- For CI and production, use environment variables or a secret manager for DB credentials. Do not commit real passwords.
