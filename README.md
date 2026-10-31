# spring-boot-sqlite-starter

A small JSON API on [Spring Boot](https://spring.io/projects/spring-boot) 4.1 (Spring MVC, `JdbcClient`) with SQLite.
Backend only. No external services: the database is a file created on first run, and the schema is applied on boot.

- `GET /up` health check, 200 only when the database answers
- `GET|POST /api/notes`, `GET|PUT|DELETE /api/notes/{id}` example CRUD resource
- JSON errors (`404`, `422`)
- Integration tests against an in-memory database

## Requirements

JDK 21 or newer. Maven is not needed: the repository carries the wrapper (`./mvnw`).

## Setup

```sh
./mvnw -DskipTests package
```

No database setup step. `data/app.db` is created next to where the app starts.

## Run

```sh
java -jar target/app.jar
```

Listens on `0.0.0.0:3000`. Check it with `curl http://localhost:3000/up`.

| Variable | Default | |
|---|---|---|
| `HOST` | `0.0.0.0` | bind address |
| `PORT` | `3000` | bind port |
| `DATABASE_PATH` | `data/app.db` | SQLite file |

## Test

```sh
./mvnw test
```

## Layout

```
src/main/java/dev/starter/app/App.java               boot
src/main/java/dev/starter/app/NotesController.java   health check and notes routes
src/main/resources/schema.sql                        idempotent schema, applied on every boot
src/test/java/dev/starter/app/NotesApiTests.java     request-level tests
```

## License

MIT
