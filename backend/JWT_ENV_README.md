JWT secret configuration

This project supports reading the JWT signing secret from an environment variable for improved security.

1) Preferred (production): set environment variable `JWT_SECRET` to a strong random value before starting the application.

Windows (CMD):

```bat
set JWT_SECRET=your-very-long-random-secret-here
mvn -DskipTests package
mvn spring-boot:run
```

PowerShell:

```powershell
$env:JWT_SECRET = 'your-very-long-random-secret-here'
mvn -DskipTests package
mvn spring-boot:run
```

Linux / macOS:

```bash
export JWT_SECRET='your-very-long-random-secret-here'
mvn -DskipTests package
mvn spring-boot:run
```

2) Fallback (development): if `JWT_SECRET` is not set, the application will use the value of `jwt.secret` in `src/main/resources/application.properties`. This is convenient for local development but insecure for production.

Notes and recommendations
- Use a secret at least 256 bits (32 bytes) for HMAC-SHA256. You can generate one with `openssl rand -base64 32`.
- Do NOT commit secrets into source control.
- Consider using a secrets manager (Vault, Azure Key Vault, AWS Secrets Manager) for production.
- When rotating secrets, you must handle token invalidation/refresh for active clients.
