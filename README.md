# Library Management System

REST API for a simple library — register borrowers and books, list books, and
borrow/return books on behalf of a borrower.

See [REQUIREMENTS.md](REQUIREMENTS.md) for a map of each task requirement to where
it's implemented, and [ASSUMPTIONS.md](ASSUMPTIONS.md) for assumptions made beyond
the task spec.

## Tech stack

- Java 17, Spring Boot 3.2 (Web, Data JPA, Validation, Actuator)
- Maven
- H2 for dev/test, PostgreSQL for prod
- JUnit 5, Mockito, MockMvc, JaCoCo

## Run

```bash
mvn spring-boot:run
```

Default profile is `dev`, port `8881`, context path `/library`.
Base URL: `http://localhost:8881/library/api/v1`

With Docker + PostgreSQL:

```bash
docker compose up --build
```

Run tests:

```bash
mvn clean verify
```

Coverage report is generated at `target/site/jacoco/index.html`.

## Configuration

Environment variables (all optional in dev):

| Variable | Default | Description |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev`, `test`, or `prod` |
| `SERVER_PORT` | `8881` | HTTP port |
| `DB_HOST` | `localhost` | Postgres host (prod) |
| `DB_PORT` | `5432` | Postgres port (prod) |
| `DB_NAME` | `librarydb` | Postgres database (prod) |
| `DB_USER` | *(required in prod)* | Postgres user |
| `DB_PASSWORD` | *(required in prod)* | Postgres password |

## API

All request/response bodies are JSON. Base path: `/library/api/v1`

### Register a borrower

`POST /library/api/v1/borrowers`

```json
{ "name": "Ada Lovelace", "email": "ada@example.com" }
```

Response `201`:
```json
{ "id": 1, "name": "Ada Lovelace", "email": "ada@example.com" }
```

`400` on invalid input or duplicate email.

### Register a book

`POST /library/api/v1/books`

```json
{ "isbn": "9780132350884", "title": "Clean Code", "author": "Robert C. Martin" }
```

Response `201`:
```json
{ "id": 1, "isbn": "9780132350884", "title": "Clean Code", "author": "Robert C. Martin", "available": true }
```

Each call creates a new copy with its own `id`. `409` if the ISBN already exists
with a different title or author.

### List all books

`GET /library/api/v1/books`

Response `200`:
```json
[
  { "id": 1, "isbn": "9780132350884", "title": "Clean Code", "author": "Robert C. Martin", "available": true },
  { "id": 2, "isbn": "9780132350884", "title": "Clean Code", "author": "Robert C. Martin", "available": false }
]
```

`available` is per copy — `false` while that copy is on loan.

### Borrow a book

`POST /library/api/v1/books/{bookId}/borrow`

```json
{ "borrowerId": 1 }
```

Response `201`:
```json
{ "id": 10, "bookId": 1, "borrowerId": 1, "borrowedAt": "2026-07-28T10:15:30", "returnedAt": null }
```

`404` if the book or borrower doesn't exist. `409` if the copy is already on loan.

### Return a book

`POST /library/api/v1/books/{bookId}/return`

```json
{ "borrowerId": 1 }
```

Response `200`:
```json
{ "id": 10, "bookId": 1, "borrowerId": 1, "borrowedAt": "2026-07-28T10:15:30", "returnedAt": "2026-07-29T09:00:00" }
```

`404` if there is no active loan for that book/borrower pair.

### Error format

All errors return the same shape:

```json
{
  "timestamp": "2026-07-28T10:15:30",
  "status": 409,
  "error": "Conflict",
  "message": "book 1 is already on loan"
}
```

Validation errors also include a `fieldErrors` array.

## Database choice

PostgreSQL (prod) — relational data (borrowers, books, loan records) with foreign
keys, and the one-active-loan-per-copy rule needs row-level locking, which an
RDBMS handles natively. H2 is used for dev/test for zero setup.

See `ASSUMPTIONS.md` for assumptions made beyond the task spec.
