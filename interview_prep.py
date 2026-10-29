"""
Generates interview_preparation_checklist.pdf — a 30-minute prep sheet for the
AEON Bank role interview on the Library Management System assessment.
"""
from fpdf import FPDF


class PDF(FPDF):
    def header(self):
        if self.page_no() == 1:
            return
        self.set_fill_color(15, 40, 80)
        self.rect(0, 0, 210, 14, "F")
        self.set_font("Helvetica", "B", 9)
        self.set_text_color(255, 255, 255)
        self.cell(0, 10, "Library Management System  -  Interview Prep Checklist (AEON Bank)",
                  align="C", new_x="LMARGIN", new_y="NEXT")
        self.set_text_color(0, 0, 0)
        self.ln(2)

    def footer(self):
        self.set_y(-12)
        self.set_font("Helvetica", "I", 8)
        self.set_text_color(120, 120, 120)
        self.cell(0, 8, f"Page {self.page_no()}", align="C")

    def section(self, title):
        self.ln(3)
        self.set_fill_color(230, 236, 245)
        self.set_font("Helvetica", "B", 12)
        self.set_text_color(15, 40, 80)
        self.cell(0, 8, f"  {title}", border=1, fill=True, new_x="LMARGIN", new_y="NEXT")
        self.set_text_color(0, 0, 0)
        self.ln(1.5)

    def sub(self, title):
        self.ln(1.5)
        self.set_font("Helvetica", "B", 10.5)
        self.set_text_color(30, 60, 110)
        self.cell(0, 6, title, new_x="LMARGIN", new_y="NEXT")
        self.set_text_color(0, 0, 0)

    def body(self, text):
        self.set_font("Helvetica", "", 9.5)
        self.multi_cell(0, 5, text)
        self.ln(0.5)

    def bullet(self, text, indent=6):
        self.set_x(self.get_x() + indent)
        self.set_font("Helvetica", "", 9.5)
        self.multi_cell(0, 5, f"-  {text}")
        self.ln(0.3)

    def code(self, text):
        self.set_x(self.get_x() + 6)
        self.set_font("Courier", "", 8.2)
        self.set_fill_color(245, 245, 248)
        self.set_text_color(30, 30, 30)
        self.multi_cell(0, 4.2, text, fill=True)
        self.set_text_color(0, 0, 0)
        self.ln(1)


pdf = PDF()
pdf.set_auto_page_break(auto=True, margin=15)
pdf.add_page()

# ---------------- TITLE ----------------
pdf.set_fill_color(15, 40, 80)
pdf.rect(0, 0, 210, 34, "F")
pdf.set_font("Helvetica", "B", 19)
pdf.set_text_color(255, 255, 255)
pdf.set_y(8)
pdf.cell(0, 9, "Library Management System", align="C", new_x="LMARGIN", new_y="NEXT")
pdf.set_font("Helvetica", "", 12)
pdf.cell(0, 6, "Interview Preparation Checklist", align="C", new_x="LMARGIN", new_y="NEXT")
pdf.set_font("Helvetica", "I", 10)
pdf.cell(0, 5, "Spring Boot 3  |  JPA / Hibernate  |  REST API  |  Target role: AEON Bank",
         align="C", new_x="LMARGIN", new_y="NEXT")
pdf.set_text_color(0, 0, 0)
pdf.ln(6)

pdf.body("How to use this sheet (30 min): Read Section 1 + 2 first (what you built & why) = 5 min. "
         "Memorise Section 3 (design patterns - they will ask names) = 5 min. Drill Section 4 "
         "(technical Q&A) = 10 min. Walk through Section 5 (live coding scenarios) = 10 min.")

# ---------------- 1. WHAT WE DID ----------------
pdf.section("1.  What We Built  (recap of the assessment)")

pdf.body("A REST API for a small library that manages three resources: Books (physical copies), "
         "Borrowers, and Borrowing (loans). Built with Spring Boot 3.2.5 / Java 17, Spring Data JPA, "
         "Bean Validation, and an H2 (dev/test) + PostgreSQL (prod) persistence layer.")

pdf.sub("Endpoints implemented")
pdf.bullet("POST /api/v1/books            - Register a book copy (ISBN + title + author).")
pdf.bullet("GET  /api/v1/books            - List all books with per-copy availability flag.")
pdf.bullet("GET  /api/v1/books/{id}       - Get a single book copy by id.")
pdf.bullet("POST /api/v1/borrowers        - Register a borrower (unique email).")
pdf.bullet("GET  /api/v1/borrowers/{id}   - Get a borrower by id.")
pdf.bullet("POST /api/v1/books/{id}/borrow   - Borrow a book on behalf of a borrower.")
pdf.bullet("POST /api/v1/books/{id}/return  - Return a book, freeing it for the next loan.")

pdf.sub("Key business rules satisfied")
pdf.bullet("One book copy can be borrowed by only one borrower at a time (409 on double-borrow).")
pdf.bullet("A registered borrower can borrow any available book copy.")
pdf.bullet("Multiple copies of the same ISBN are allowed, BUT title & author must match (409 on "
           "ISBN reuse for a different book).")
pdf.bullet("Returning a book frees it; the same copy can then be borrowed again.")
pdf.bullet("Borrower email is unique (case-insensitive, stored lowercased).")

pdf.sub("Non-functional work")
pdf.bullet("Layered architecture: Controller -> Service (interface + impl) -> Repository -> Entity.")
pdf.bullet("DTOs separate the API contract from the JPA entities (BookRequest/Response, etc.).")
pdf.bullet("Global exception handler (@RestControllerAdvice) -> one consistent error envelope.")
pdf.bullet("Bean Validation on all request DTOs (@NotBlank, @Email, @Pattern for ISBN).")
pdf.bullet("Pessimistic locking (SELECT ... FOR UPDATE) for concurrent borrow safety.")
pdf.bullet("OpenAPI / Swagger UI documentation via springdoc.")
pdf.bullet("Spring Boot Actuator health + info endpoints.")
pdf.bullet("Unit tests (Mockito) + integration tests (MockMvc @SpringBootTest) + a concurrency test.")
pdf.bullet("JaCoCo code coverage, Maven Surefire + Failsafe split (unit vs *IT integration tests).")
pdf.bullet("Multi-stage Dockerfile (build on maven image, run on JRE, non-root user).")
pdf.bullet("docker-compose.yml wiring app + Postgres; GitHub Actions CI pipeline.")

# ---------------- 2. WHY WE CHOSE ----------------
pdf.section("2.  Why We Chose These Technologies / Approaches")

pdf.bullet("Spring Boot 3 + Java 17: industry standard, fast startup, opinionated defaults; "
           "Java 17 gives records, sealed classes, pattern matching, modern switch.")
pdf.bullet("Spring Data JPA: removes boilerplate CRUD; repository interfaces auto-generate SQL. "
           "Lets us hand-write @Query + @Lock where we need control (pessimistic locking).")
pdf.bullet("H2 in-memory for dev/test, PostgreSQL for prod: zero-friction local dev + a real "
           "RDBMS in prod. Same code, profile-driven datasource via application-{profile}.yml.")
pdf.bullet("Pessimistic (not optimistic) locking for borrow: two librarians clicking 'borrow' on the "
           "same copy at once MUST not both succeed. SELECT FOR UPDATE on the book row serialises them; "
           "optimistic @Version would retry/conflict but the borrow is a one-shot user action.")
pdf.bullet("DTO + Response.from() static factory: keeps entities internal; we never expose JPA proxies "
           "to the API, avoiding lazy-initialisation exceptions and accidental field leaks.")
pdf.bullet("@RestControllerAdvice + ApiResponse envelope: every error has the same shape "
           "(timestamp, status, error, message, fieldErrors) - easy for clients to parse.")
pdf.bullet("BaseEntity @MappedSuperclass: id, createdDate, lastModifiedDate, @Version are shared by "
           "all entities - no duplication, consistent auditing.")
pdf.bullet("springdoc OpenAPI: live Swagger UI at runtime; annotations double as documentation.")
pdf.bullet("Failsafe for *IT tests: keeps slow integration tests out of the unit-test phase, matching "
           "Maven convention; CI runs 'mvn verify' so both execute.")
pdf.bullet("Multi-stage Docker + non-root user: smaller image, no JDK in runtime, security best practice.")

# ---------------- 3. DESIGN PATTERNS ----------------
pdf.section("3.  Technical Designs / Design Patterns Used  (memorise the names)")

pdf.body("There are roughly 10 named patterns/principles in this codebase. Interviewers love names:")
pdf.bullet("1. Layered (n-tier) Architecture - Controller / Service / Repository / Entity separation.")
pdf.bullet("2. DTO Pattern (Data Transfer Object) - BookRequest, BookResponse, etc. isolate the API.")
pdf.bullet("3. Service Interface + Implementation - BookService (interface) + BookServiceImpl. "
           "Enables mocking, swap of impl, program-to-interface.")
pdf.bullet("4. Repository Pattern - Spring Data JPA repositories abstract persistence.")
pdf.bullet("5. Builder Pattern - Lombok @Builder on DTOs / ApiResponse; fluent object construction.")
pdf.bullet("6. Static Factory Method - BookResponse.from(book, available), BorrowRecordResponse.from(record).")
pdf.bullet("7. Front Controller - Spring DispatcherServlet routes all requests (framework-provided).")
pdf.bullet("8. Centralised / Global Exception Handling - @RestControllerAdvice + @ExceptionHandler.")
pdf.bullet("9. Template Method / Inheritance - BaseEntity @MappedSuperclass holds common audit fields.")
pdf.bullet("10. Pessimistic Locking (concurrency control) - @Lock(PESSIMISTIC_WRITE) / SELECT FOR UPDATE.")
pdf.bullet("Bonus: Optimistic Locking primitive present - @Version in BaseEntity (JPA optimistic lock).")
pdf.bullet("Bonus: Dependency Injection / Constructor injection (no @Autowired on fields - testable).")
pdf.bullet("Bonus: Single Responsibility - ApiConstants, FieldError each do one thing.")

# ---------------- 4. INTERVIEW Q&A ----------------
pdf.section("4.  Likely Technical Questions  (AEON Bank - financial context)")

pdf.sub("Architecture & general")
pdf.bullet("Q: Walk me through your architecture.  A: Layered Spring Boot app. Controllers expose "
           "REST under /api/v1, delegate to service interfaces, which call JPA repositories. DTOs "
           "decouple the API from entities. A global advice normalises errors. Postgres in prod, H2 in test.")
pdf.bullet("Q: Why interfaces for services when there's one impl?  A: Program to interface; easier "
           "mocking in unit tests, clear contract, room to add a second impl (e.g. caching) later.")
pdf.bullet("Q: How would you scale this for AEON Bank's traffic?  A: Stateless app -> horizontal scale "
           "behind a load balancer; externalise session (none here, good); add a cache (Redis) for hot "
           "reads like book listings; read replicas for GETs; DB connection pooling (Hikari already in).")

pdf.sub("Concurrency (most likely deep-dive)")
pdf.bullet("Q: Two users borrow the same book at once - what happens?  A: Pessimistic write lock on "
           "the book row (SELECT ... FOR UPDATE). Second request blocks until first commits, then sees "
           "the active loan and gets 409. Exactly one succeeds - proven by BorrowingConcurrencyIT.")
pdf.bullet("Q: Why lock the book row, not the loan row?  A: When the copy is free, the active-loan "
           "query returns no rows and locks nothing (Postgres has no gap locking) - both requests would "
           "slip through. Locking the book row always acquires a lock, serialising the check-then-insert.")
pdf.bullet("Q: Pessimistic vs optimistic locking?  A: Pessimistic locks upfront (good for high "
           "contention, like borrowing). Optimistic uses @Version, no lock, retries on conflict - good "
           "for low contention. We have @Version in BaseEntity as the optimistic primitive too.")
pdf.bullet("Q: What if the lock holder crashes?  A: The DB transaction rolls back, lock is released, "
           "blocked request proceeds. Set a lock timeout to avoid indefinite waits.")

pdf.sub("JPA / Hibernate")
pdf.bullet("Q: N+1 problem - did you handle it?  A: Yes. The book list fetches borrowed book ids in "
           "ONE query (findBookIdsWithActiveLoan) and maps availability from the Set, instead of one "
           "query per book.")
pdf.bullet("Q: Why LAZY on BorrowRecord.book/borrower?  A: Avoid loading related entities unless "
           "needed; prevents eager joins on every loan read. DTO uses .getId() so no lazy init issue.")
pdf.bullet("Q: ddl-auto=update in prod - ok?  A: No for real prod. I'd use Flyway/Liquibase with "
           "ddl-auto=validate. Left as update here to keep the exercise self-contained (noted in code).")

pdf.sub("REST & validation")
pdf.bullet("Q: How are validation errors returned?  A: @Valid triggers MethodArgumentNotValidException; "
           "the advice maps it to 400 with a fieldErrors array (field, message, objectName, rejectedValue).")
pdf.bullet("Q: HTTP status codes used?  A: 201 create, 200 read/return, 400 validation, 404 not found, "
           "409 conflict (double-borrow / ISBN reuse), 405 method not supported, 500 unhandled.")
pdf.bullet("Q: Is your API idempotent?  A: Borrow is not idempotent (calling twice = two loans, but "
           "second is blocked by the active-loan check). Return is effectively idempotent - returning "
           "an already-returned book yields 404 'no active loan'. Could add an idempotency-key header.")

pdf.sub("Testing")
pdf.bullet("Q: Test strategy?  A: Unit tests (Mockito, no Spring) for service logic; integration tests "
           "(MockMvc + real H2) for controller->db; a CountDownLatch concurrency test proving the lock.")
pdf.bullet("Q: Why failsafe?  A: Surefire runs unit tests; *IT classes run in integration-test phase "
           "via Failsafe. 'mvn verify' runs both; 'mvn test' runs only unit tests.")

# ---------------- 5. LIVE CODING SCENARIOS ----------------
pdf.section("5.  Live-Coding Scenarios  ('improve a feature - show me the code')")

pdf.sub("A) Add Security to the API  (most asked for a bank)")
pdf.body("Approach: spring-boot-starter-security + JWT. Stateless, role-based (LIBRARIAN vs BORROWER).")
pdf.code("<!-- pom.xml -->\n"
         "<dependency>\n"
         "  <groupId>org.springframework.boot</groupId>\n"
         "  <artifactId>spring-boot-starter-security</artifactId>\n"
         "</dependency>\n"
         "<dependency>\n"
         "  <groupId>org.springframework.boot</groupId>\n"
         "  <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>\n"
         "</dependency>")
pdf.code("@Configuration\n"
         "@EnableWebSecurity\n"
         "public class SecurityConfig {\n"
         "  @Bean\n"
         "  public SecurityFilterChain chain(HttpSecurity http) throws Exception {\n"
         "    http.csrf(c->c.disable())\n"
         "      .sessionManagement(s->s.sessionCreationPolicy(STATELESS))\n"
         "      .authorizeHttpRequests(a -> a\n"
         "        .requestMatchers(\"/api/v1/books/**\",\"/api/v1/borrowers/**\").hasRole(\"LIBRARIAN\")\n"
         "        .requestMatchers(\"/api/v1/books/*/borrow\",\"/api/v1/books/*/return\").hasRole(\"BORROWER\")\n"
         "        .anyRequest().authenticated())\n"
         "      .oauth2ResourceServer(o->o.jwt(Customizer.withDefaults()));\n"
         "    return http.build();\n"
         "  }\n"
         "}")
pdf.bullet("Mention: input validation (already done), rate limiting (Bucket4j/Resilience4j), "
           "HTTPS/TLS, secrets in a vault (not in docker-compose), audit logging of borrows.")
pdf.bullet("For AEON: PCI-DSS / data encryption at rest, PII handling for borrower email, "
           "OWASP Top 10 (injection - we use parameterised queries via JPA, so safe).")

pdf.sub("B) Add Pagination + Sorting to GET /books")
pdf.code("@GetMapping\n"
         "public ResponseEntity<Page<BookResponse>> getAll(\n"
         "    @RequestParam(defaultValue=\"0\") int page,\n"
         "    @RequestParam(defaultValue=\"20\") int size,\n"
         "    @RequestParam(defaultValue=\"title\") String sort,\n"
         "    @RequestParam(defaultValue=\"asc\") String dir) {\n"
         "  Pageable p = PageRequest.of(page, size,\n"
         "      Sort.by(Sort.Direction.fromString(dir), sort));\n"
         "  return ResponseEntity.ok(bookService.getAll(p));\n"
         "}")
pdf.code("// repository + service\n"
         "Page<Book> findAll(Pageable p);   // already in JpaRepository")

pdf.sub("C) Add a 'search by title/author' endpoint")
pdf.code("// BookRepository\n"
         "Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(\n"
         "    String title, String author, Pageable p);")
pdf.bullet("Or a JPA Specification for dynamic multi-field search; or full-text if scaling up.")

pdf.sub("D) Add overdue-loan tracking + scheduled job")
pdf.code("@Entity\n"
         "public class BorrowRecord {\n"
         "  private LocalDate dueDate;        // borrowedAt + 14 days\n"
         "  private boolean overdue;\n"
         "}\n"
         "@Scheduled(cron = \"0 0 1 * * *\")\n"
         "public void markOverdueLoans() {\n"
         "  repo.findActiveLoansDueBefore(LocalDate.now())\n"
         "      .forEach(r -> r.setOverdue(true));\n"
         "}")
pdf.bullet("Enable with @EnableScheduling; add a fine/notification event for AEON-style reminders.")

pdf.sub("E) Add caching for hot reads (Redis)")
pdf.code("@Cacheable(value=\"books\", key=\"#id\")\n"
         "public Book getById(Long id) { ... }\n"
         "@CacheEvict(value=\"books\", key=\"#book.id\")\n"
         "public Book register(Book book) { ... }")
pdf.bullet("Mention cache invalidation on borrow/return; TTL; Caffeine for local, Redis for distributed.")

pdf.sub("F) Add API versioning / idempotency")
pdf.bullet("Versioning: path (/api/v1) already done; could add header or content negotiation.")
pdf.bullet("Idempotency: accept Idempotency-Key header, store request+response in Redis for 24h; "
           "replay same response on duplicate. Critical for payment-style endpoints at a bank.")

pdf.sub("G) Add a DB migration tool (Flyway)")
pdf.bullet("Add flyway-core dep, set spring.flyway.enabled=true, ddl-auto=validate. Put V1__init.sql "
           "in src/main/resources/db/migration. Every schema change = a new versioned, reviewed SQL file.")

# ---------------- 6. BEHAVIOURAL / 30s ANSWERS ----------------
pdf.section("6.  30-Second 'Elevator' Answers  (if they ask quickly)")

pdf.bullet("Biggest challenge: 'Concurrent borrowing. I chose pessimistic locking on the book row "
           "because optimistic locking would let a retry through, and locking the loan row fails when "
           "the copy is free. A CountDownLatch test proves exactly one of two simultaneous borrows wins.'")
pdf.bullet("Trade-off you accepted: 'ddl-auto=update in prod for exercise simplicity; I flagged it and "
           "would replace it with Flyway + validate in a real release.'")
pdf.bullet("What you'd improve next: 'Add Spring Security + JWT, pagination, Redis caching for book "
           "listings, Flyway migrations, and an idempotency key on borrow/return.'")
pdf.bullet("Why this fits AEON Bank: 'Same concerns a bank API has - concurrency on shared state "
           "(like an account balance), audit trails, idempotent money-movement, strict validation, "
           "and security. The borrow flow is structurally similar to a debit transaction.'")

pdf.ln(2)
pdf.set_font("Helvetica", "I", 8.5)
pdf.set_text_color(90, 90, 90)
pdf.multi_cell(0, 5,
    "Tip: if asked to code live, narrate the trade-off out loud (why pessimistic, why DTO, why "
    "@Transactional). Interviewers score reasoning as much as the code. Good luck!")

out = r"D:\Spring\library-management-system\interview_preparation_checklist.pdf"
pdf.output(out)
print("PDF written to:", out)
print("Pages:", pdf.page_no())
