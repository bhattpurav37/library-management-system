# Requirements Coverage

A map from each task line item to where it's implemented in this repo.

## API actions

| Action | Endpoint | Code |
|---|---|---|
| Register a borrower | `POST /library/api/v1/borrowers` | `BorrowerController.register` → `BorrowerServiceImpl.register` |
| Register a book | `POST /library/api/v1/books` | `BookController.register` → `BookServiceImpl.register` |
| List all books | `GET /library/api/v1/books` | `BookController.getAll` → `BookServiceImpl.getAll` |
| Borrow a book (on behalf of a borrower) | `POST /library/api/v1/books/{bookId}/borrow` | `BorrowingServiceImpl.borrow` |
| Return a borrowed book | `POST /library/api/v1/books/{bookId}/return` | `BorrowingServiceImpl.returnBook` |

## Data models

| Requirement | How it's modeled / enforced |
|---|---|
| Borrower: unique id, name, email | `Borrower` entity (`id`, `name`, `email`) |
| Book: unique id, ISBN, title, author | `Book` entity (`id`, `isbn`, `title`, `author`) |
| Same ISBN must share title/author | `BookServiceImpl.register` checks existing copies; `409` on mismatch |
| Multiple copies of same ISBN allowed | Each `POST /books` creates a new row with its own `id` |

## Requirements (1–10)

| # | Requirement | Where |
|---|---|---|
| 1 | Language/framework of choice (Java 17 + Spring Boot = bonus) | Java 17, Spring Boot 3.2 — `pom.xml` |
| 2 | Configurable across environments | `dev` / `test` / `prod` profiles in `src/main/resources/` |
| 3 | Package manager for dependencies | Maven — `pom.xml` |
| 4 | Data validation + error handling | `@Valid` on request DTOs; global `ExceptionTranslatorAdvice` |
| 5 | Database + justification | PostgreSQL (prod) / H2 (dev/test); justification in README "Database choice" |
| 6 | REST endpoints for each action | See "API actions" above |
| 7 | Same ISBN → books with different ids | `BookServiceImpl.register` (every call inserts a new copy) |
| 8 | No more than one borrower per book id at a time | `BorrowingServiceImpl.borrow` — pessimistic row lock on the Book; proven by `BorrowingConcurrencyIT` |
| 9 | API documentation | README + Swagger UI at `/library/swagger-ui/index.html` (OpenAPI config in `OpenApiConfig`) |
| 10 | Assumptions documented | `ASSUMPTIONS.md` |

## Nice to have

| # | Item | Where |
|---|---|---|
| 1 | Unit tests + coverage | Unit: `BookServiceTest`, `BorrowingServiceTest`; integration: `BookControllerIT`, `BorrowingControllerIT`, `BorrowingConcurrencyIT`; JaCoCo report on `mvn verify` |
| 2 | Clean code | Layered architecture (controller → service → repository), DTOs, global exception handler, Bean Validation |
| 3 | Containerization + CI/CD (declarative) | `Dockerfile` (multi-stage), `docker-compose.yml`, `k8s/` manifests, `.github/workflows/ci.yml` |
| 4 | 12-factor conformance | Config via env vars (Factor III), declared deps, stateless, stdout logs, build/run separation |
