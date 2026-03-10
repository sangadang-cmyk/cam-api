# GitHub Copilot Instructions — cam-api

## Project Overview
Spring Boot 4 REST API

**Stack:** Java 25 · Spring Boot 4 · Spring Data JPA · SQL Server (Azure) · Spring Security OAuth2 Resource Server · Azure Blob Storage (`spring-cloud-azure-starter-storage-blob`) · MapStruct · Lombok · SpringDoc OpenAPI 3 · Hibernate Validator

---

## Package Structure

Every feature module lives under `tech.sangdang.camapi.modules.<ModuleName>/` and is divided into four layers:

```
modules/
  <ModuleName>/
    api/          # Controller interface + @RestController implementation
    app/
      dto/
        command/  # Write-side input DTOs
        query/    # Read-side input DTOs
        res/      # Response DTOs
      mapper/     # MapStruct mappers
      service/    # Service interfaces
        impl/     # Service implementations
    domain/
      constants/  # Enums
      repository/ # Spring Data JPA repositories
      <Entity>.java
    infra/        # JPA converters, infrastructure adapters
```

Shared cross-cutting code lives under `tech.sangdang.camapi.common/`:

```
common/
  core/
    BusinessError.java          # Abstract base for domain exceptions
    ResourceNotFoundError.java  # 404 exception
    ErrorResponse.java
    validation/
      ValidEnum.java            # Custom enum constraint annotation
      ValidEnumValidator.java   # Constraint validator implementation
  infra/
    BusinessErrorProcessor.java
    ConstraintErrorProcessor.java
    SpringDocCustomOAuthProperties.java
```

Spring configuration beans live under `tech.sangdang.camapi.config/`.

---

## Coding Conventions

### DTOs

**Command DTOs** (write-side input):
- Annotate with `@Data @NoArgsConstructor @SuperBuilder(toBuilder = true)`
- Place in `app/dto/command/`
- Apply Hibernate Validator constraints directly on fields
- Use `@ValidEnum(enumClass = MyEnum.class)` for enum fields — never `@Size` or string-based enum checks
- For list elements: `List<@ValidEnum(enumClass = Foo.class) Foo>`
- Inner static classes (e.g. `TargetUser`) also get `@Data @NoArgsConstructor @SuperBuilder(toBuilder = true)`

**Query DTOs** (read-side input):
- Annotate with `@Data @NoArgsConstructor @SuperBuilder(toBuilder = true)`
- Place in `app/dto/query/`
- One query class per service method

**Response DTOs**:
- Annotate with `@Data @NoArgsConstructor @AllArgsConstructor @Builder`
- Place in `app/dto/res/`
- No JPA annotations — plain Java types only
- Enum fields are mapped to `String` (via MapStruct expression)

### Service Interfaces
- Annotate with `@Validated`
- Each method takes a **single** DTO parameter annotated with `@Valid`
- Place in `app/service/`

```java
@Validated
public interface MyService {
    MyResponse doSomething(@Valid MyCommand command);
}
```

### Service Implementations
- Annotate with `@Slf4j @RequiredArgsConstructor @Service`
- Implement the service interface
- Place in `app/service/impl/`
- Write operations: `@Transactional`; read operations: `@Transactional(readOnly = true)`
- Throw `ResourceNotFoundError("EntityName", id)` when a record is not found by ID

### Controllers
- **Interface** in `api/` — carries `@Tag`, `@RequestMapping`, `@Operation`, HTTP method annotations, and `@ResponseStatus`. No validation annotations.
- **Implementation** in `api/` — annotated with `@RestController @RequiredArgsConstructor`, implements the interface. No Spring MVC annotations (inherited from interface). Delegates entirely to injected services.
- The impl constructs the appropriate DTO/command/query in the method body and calls the service.

```java
// Interface
@Tag(name = "My Module")
@RequestMapping("/api/admin/my-module")
public interface MyController {
    @Operation(summary = "...")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    MyResponse create(@RequestBody CreateCommand command);
}

// Implementation
@RestController
@RequiredArgsConstructor
public class MyControllerImpl implements MyController {
    private final MyManagementService managementService;

    @Override
    public MyResponse create(CreateCommand command) {
        return managementService.create(command);
    }
}
```

### MapStruct Mappers
- Annotate with `@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)`
- Place in `app/mapper/`
- Partial updates use `void updateFromCommand(UpdateCommand command, @MappingTarget Entity entity)` — null source fields are ignored
- Map enum fields to String with a java expression: `@Mapping(target = "field", expression = "java(entity.getEnum() != null ? entity.getEnum().name() : null)")`

### Domain Entities
- Annotate with `@Getter @Setter @NoArgsConstructor @SuperBuilder(toBuilder = true) @Entity @Table`
- All `@SuperBuilder` usages include `toBuilder = true`
- Use `@GeneratedValue(strategy = GenerationType.UUID)` for String IDs
- Audit fields (`createdAt`, `updatedAt`) use `@CreatedDate` / `@LastModifiedDate`

### Error Handling
- Domain exceptions extend `BusinessError` (abstract)
- `BusinessError` subclasses override `getHttpStatus()` and `getErrorCode()`
- Always use `ResourceNotFoundError("EntityName", id)` for 404s — never return null or throw generic exceptions
- Custom errors go in `common/core/`

### Validation
- Service layer validation via `@Validated` + `@Valid` — do **not** put `@Valid` on controller method parameters
- Use `@ValidEnum` from `common/core/validation/` for all enum fields — never validate enums as strings
- Use `@NotEmpty` for required collections, `@NotBlank` for required strings, `@NotNull` for required objects

---

## Azure Blob Storage

- Dependency: `spring-cloud-azure-starter-storage-blob` (version managed by `spring-cloud-azure-dependencies` BOM)
- `BlobServiceClient` is **auto-configured** by the starter — never declare a manual `@Bean` for it
- Connection is set via `spring.cloud.azure.storage.blob.connection-string` in `application.yml`
- App-level settings (container name, SAS duration) live under `app.azure.blob.*`
- SAS URLs are generated using `BlobSasPermission` + `BlobServiceSasSignatureValues` with write-only permission (`setWritePermission(true)`)
- Blob names are scoped per upload request: `{uploadRequestId}`

---

## Security

- OAuth2 Resource Server (JWT) — configured via `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- Swagger UI supports both **client credentials** and **authorization code** flows
- OAuth client credentials for Swagger are injected via environment variables (`SWAGGER_OAUTH_CLIENT_ID`, `SWAGGER_OAUTH_CLIENT_SECRET`, etc.) — never hardcoded

---

## Configuration & Secrets Policy

- **Never hardcode** passwords, keys, connection strings, or secrets in any source file
- All secrets must be referenced via `${ENV_VAR_NAME}` placeholders in `application.yml`
- `terraform.tfstate` and `terraform.tfvars` must be gitignored — they contain plaintext secrets
- Azure Storage account keys and DB passwords must be rotated if ever committed to source control

---

## application.yml Namespace Summary

| Namespace | Purpose |
|---|---|
| `spring.cloud.azure.storage.blob.*` | Azure Blob Storage autoconfiguration (starter) |
| `app.azure.blob.*` | App-level blob settings (container name, SAS duration) |
| `spring.security.oauth2.resourceserver.jwt.*` | JWT resource server config |
| `custom.springdoc.swagger-ui.oauth.*` | Swagger UI OAuth settings (read by `SpringDocConfig`) |
| `spring.datasource.*` | SQL Server datasource |

---

## Conventions Summary Checklist

When generating a new module, always produce:
1. `domain/<Entity>.java` — JPA entity
2. `domain/repository/<Entity>Repository.java` — Spring Data repo
3. `domain/constants/` — any enums
4. `app/dto/command/Create<Entity>Command.java` — with `@NoArgsConstructor @SuperBuilder(toBuilder = true)`
5. `app/dto/command/Update<Entity>Command.java` — with `@NoArgsConstructor @SuperBuilder(toBuilder = true)`, includes `@NotBlank String id`
6. `app/dto/query/FindAll<Entity>sQuery.java` + `FindById<Entity>Query.java`
7. `app/dto/res/<Entity>Response.java` — with `@NoArgsConstructor @AllArgsConstructor @Builder`
8. `app/mapper/<Entity>Mapper.java` — MapStruct, `NullValuePropertyMappingStrategy.IGNORE`
9. `app/service/<Entity>ManagementService.java` + `<Entity>QueryService.java` — `@Validated`, single `@Valid` param per method
10. `app/service/impl/<Entity>ManagementServiceImpl.java` + `<Entity>QueryServiceImpl.java` — `@Slf4j @RequiredArgsConstructor @Service`
11. `api/<Entity>AdminController.java` — interface with Swagger annotations only
12. `api/<Entity>AdminControllerImpl.java` — `@RestController @RequiredArgsConstructor`, delegates to services

