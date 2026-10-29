"""
AEON Digital Bank – 60-Min Technical Interview Answer Key & Checklist
Generates a polished PDF with short, senior-level answers.
"""

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm, cm
from reportlab.lib.colors import HexColor, black, white
from reportlab.lib.enums import TA_LEFT, TA_CENTER, TA_JUSTIFY
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle,
    PageBreak, KeepTogether, HRFlowable, ListFlowable, ListItem
)
from reportlab.platypus.flowables import Flowable
import os

OUTPUT = os.path.join(os.path.dirname(__file__), "AEON_Interview_Answer_Key.pdf")

# ── Colours ──────────────────────────────────────────────
C_PRIMARY   = HexColor("#1B3A5C")   # dark navy
C_ACCENT    = HexColor("#2E86C1")   # bright blue
C_LIGHT_BG  = HexColor("#EBF5FB")   # light blue cell bg
C_WARN_BG   = HexColor("#FEF9E7")   # warm yellow bg
C_GREEN_BG  = HexColor("#EAFAF1")   # green tint
C_DARK_TEXT  = HexColor("#1C2833")
C_MID_TEXT   = HexColor("#2C3E50")
C_GRAY_LINE = HexColor("#AEB6BF")

# ── Styles ───────────────────────────────────────────────
base = getSampleStyleSheet()

sTitle = ParagraphStyle("Title2", parent=base["Title"],
    fontSize=20, leading=24, textColor=C_PRIMARY,
    spaceAfter=4*mm, alignment=TA_CENTER)

sSubtitle = ParagraphStyle("Sub", parent=base["Normal"],
    fontSize=10, leading=13, textColor=C_ACCENT,
    spaceAfter=8*mm, alignment=TA_CENTER)

sH1 = ParagraphStyle("H1", parent=base["Heading1"],
    fontSize=14, leading=18, textColor=C_PRIMARY,
    spaceBefore=8*mm, spaceAfter=3*mm,
    borderPadding=(0,0,2,0))

sH2 = ParagraphStyle("H2", parent=base["Heading2"],
    fontSize=11.5, leading=15, textColor=C_ACCENT,
    spaceBefore=5*mm, spaceAfter=2*mm)

sBody = ParagraphStyle("Body2", parent=base["Normal"],
    fontSize=9.5, leading=13, textColor=C_DARK_TEXT,
    alignment=TA_JUSTIFY, spaceAfter=2*mm)

sBullet = ParagraphStyle("Bullet2", parent=sBody,
    leftIndent=14, bulletIndent=4, spaceAfter=1.5*mm)

sBulletInner = ParagraphStyle("BulletInner", parent=sBody,
    leftIndent=28, bulletIndent=18, fontSize=9, spaceAfter=1*mm)

sKeyAnswer = ParagraphStyle("KeyAns", parent=sBody,
    leftIndent=14, fontSize=9.5, textColor=HexColor("#1A5276"),
    backColor=C_LIGHT_BG, borderPadding=(3,4,3,4),
    spaceAfter=2.5*mm)

sWarnBox = ParagraphStyle("Warn", parent=sBody,
    leftIndent=14, fontSize=9.5, textColor=HexColor("#7D6608"),
    backColor=C_WARN_BG, borderPadding=(3,4,3,4),
    spaceAfter=2.5*mm)

sGreenBox = ParagraphStyle("Green", parent=sBody,
    leftIndent=14, fontSize=9.5, textColor=HexColor("#1E8449"),
    backColor=C_GREEN_BG, borderPadding=(3,4,3,4),
    spaceAfter=2.5*mm)

sChecklist = ParagraphStyle("Check", parent=sBody,
    leftIndent=14, fontSize=9.5, spaceAfter=1.5*mm)

sTableCell = ParagraphStyle("TC", parent=sBody,
    fontSize=9, leading=12, spaceAfter=0)

sTableHeader = ParagraphStyle("TH", parent=sTableCell,
    textColor=white, fontSize=9.5, fontName="Helvetica-Bold")


# ── Helpers ──────────────────────────────────────────────
def h1(t):   return Paragraph(t, sH1)
def h2(t):   return Paragraph(t, sH2)
def p(t):    return Paragraph(t, sBody)
def b(t):    return Paragraph(f"• {t}", sBullet)
def bi(t):   return Paragraph(f"◦ {t}", sBulletInner)
def ans(t):  return Paragraph(f"<b>✔ Answer:</b> {t}", sKeyAnswer)
def warn(t): return Paragraph(f"<b>⚠ Key Point:</b> {t}", sWarnBox)
def tip(t):  return Paragraph(f"<b>\U0001F4A1 Pro-tip:</b> {t}", sGreenBox)
def chk(t):  return Paragraph(f"☐ {t}", sChecklist)
def sp(h=3): return Spacer(1, h*mm)
def hr():    return HRFlowable(width="100%", thickness=0.5, color=C_GRAY_LINE, spaceAfter=3*mm, spaceBefore=2*mm)

def table(headers, rows, col_widths=None):
    """Builds a styled table."""
    hp = [Paragraph(h, sTableHeader) for h in headers]
    data = [hp]
    for row in rows:
        data.append([Paragraph(str(c), sTableCell) for c in row])
    w = col_widths or [None]*len(headers)
    t = Table(data, colWidths=w, repeatRows=1)
    t.setStyle(TableStyle([
        ("BACKGROUND", (0,0), (-1,0), C_PRIMARY),
        ("TEXTCOLOR", (0,0), (-1,0), white),
        ("ROWBACKGROUNDS", (0,1), (-1,-1), [white, C_LIGHT_BG]),
        ("GRID", (0,0), (-1,-1), 0.4, C_GRAY_LINE),
        ("VALIGN", (0,0), (-1,-1), "TOP"),
        ("TOPPADDING", (0,0), (-1,-1), 3),
        ("BOTTOMPADDING", (0,0), (-1,-1), 3),
        ("LEFTPADDING", (0,0), (-1,-1), 4),
        ("RIGHTPADDING", (0,0), (-1,-1), 4),
    ]))
    return t


# ══════════════════════════════════════════════════════════
# CONTENT SECTIONS
# ══════════════════════════════════════════════════════════

def section_cover():
    return [
        sp(15),
        Paragraph("AEON Digital Bank", ParagraphStyle("CoverOrg", parent=sTitle,
            fontSize=14, textColor=C_ACCENT, spaceAfter=2*mm)),
        Paragraph("60-Minute Technical Interview<br/>Answer Key &amp; Checklist", sTitle),
        Paragraph("Java Backend Developer | Senior/Lead Level<br/>Short, sharp, senior-sounding answers", sSubtitle),
        hr(),
        Paragraph("<b>Golden Rule:</b> For every senior question answer with → "
                   "<b>Concept → Real example → Trade-off → Failure scenario → Monitoring.</b><br/>"
                   "For digital banking, always emphasize: <b>security, correctness, idempotency, "
                   "transaction integrity, scalability, observability, failure handling.</b>", sWarnBox),
        sp(5),
    ]

def section_game_plan():
    return [
        h1("1. 60-Minute Interview Game Plan"),
        table(
            ["Time", "Area", "Focus / Talking Points"],
            [
                ["0–5 min", "Intro",
                 "12+ yrs Java, Tech Lead, Spring Boot, APIs, Kafka, K8s, AWS."],
                ["5–15 min", "Project Deep-Dive",
                 "YTL/ZSmart BSS transformation; API design, challenges, prod support."],
                ["15–25 min", "Java / Spring",
                 "Core Java, concurrency, Spring Boot, transactions, JPA, REST."],
                ["25–35 min", "Architecture",
                 "DDD, microservices, Kafka, idempotency, Outbox, Saga, resilience."],
                ["35–45 min", "DB / Security / Cloud",
                 "PostgreSQL, MongoDB, OWASP, Kubernetes, AWS."],
                ["45–55 min", "Coding",
                 "1–2 practical Java problems; complexity + edge cases."],
                ["55–60 min", "Assessment / Closing",
                 "Library system review, trade-offs, your questions for interviewer."],
            ],
            col_widths=[55, 75, 340]
        ),
        tip("Spend the first 5 min establishing credibility. Drop specific numbers: "
            "'3M+ API calls/day', 'p99 < 200ms', 'reduced latency 40%'."),
        sp(),
    ]

def section_intro():
    return [
        h1("2. Tell Me About Yourself — 90 Seconds"),
        ans("12+ years in Java/Java EE, mostly telecom BSS/OSS. Currently a Tech Lead — "
            "backend &amp; API development with Spring Boot, REST, Kafka, Docker/K8s, AWS. "
            "Led the YTL BSS transformation: APIs integrating frontend with ZSmart BSS. "
            "I own design discussions, code reviews, production support, and mentor the team. "
            "I’m here because I want to bring that experience into digital banking — "
            "where correctness, security, and resilience matter most."),
        warn("Structure: Experience → Current role → Strengths → Key project "
             "→ Leadership → Why this role. Don’t ramble."),
    ]

def section_project_deep_dive():
    return [
        h1("3. Project Deep-Dive Checklist"),
        b("<b>End-to-end API:</b> Request validation → Auth check → Business logic "
          "→ Downstream call → Response/error mapping → Logging &amp; metrics."),
        b("<b>Production issue:</b> ‘We saw p99 spike on the billing API. Root cause: "
          "connection pool exhaustion under load. Fix: increased pool, added circuit breaker, "
          "set meaningful timeouts. Monitored via Grafana dashboards.’"),
        b("<b>Performance improvement:</b> ‘N+1 on loan queries → replaced with "
          "JOIN FETCH + DTO projection. Went from 120 queries per page to 1.’"),
        b("<b>Kafka usage:</b> ‘Async event publishing for downstream notifications. "
          "Decouples services, handles bursts, provides at-least-once with idempotent consumers.’"),
        b("<b>Timeouts &amp; retries:</b> ‘Exponential backoff with jitter. Circuit breaker "
          "opens after 5 failures. Retry only on transient errors, never on 4xx.’"),
        b("<b>Separate YOUR contribution:</b> ‘I designed the API contract and led the "
          "implementation of the core flow; the team built the UI integration.’"),
        warn("Always own your part clearly. ‘We did X’ is vague; "
             "‘I designed and led X; the team implemented Y’ is credible."),
    ]

def section_java():
    return [
        h1("4. Java — Highest Priority"),

        h2("4.1 OOP + SOLID"),
        b("<b>SRP:</b> One reason to change. Example: separate LoanService from FineService."),
        b("<b>OCP:</b> Open for extension, closed for modification. Use Strategy for new fine rules."),
        b("<b>LSP:</b> Subtypes must be substitutable. Don’t override to throw UnsupportedOperationException."),
        b("<b>ISP:</b> Small, focused interfaces. Split a fat PaymentService into Payable, Refundable, Verifiable."),
        b("<b>DIP:</b> Depend on abstractions. Inject FineCalculator interface, not a specific impl."),

        h2("4.2 HashMap Internals"),
        ans("Array of buckets (Node[]). hash(key) → bucket index. Collisions: linked list → "
            "treeified to Red-Black tree at threshold 8. Default capacity 16, load factor 0.75 → "
            "rehash to double capacity. Not thread-safe."),
        b("<b>HashMap vs ConcurrentHashMap:</b> CHS segments the table (or uses CAS on nodes since Java 8). "
          "No full-table lock → concurrent reads/writes. Null keys/values NOT allowed in CHS."),

        h2("4.3 equals() &amp; hashCode()"),
        ans("Contract: equal objects MUST have same hashcode; unequal objects SHOULD have different hashcodes. "
            "If you override equals, always override hashCode. Immutable objects (String, Integer) are safe "
            "HashMap keys. Never use mutable fields in hashCode — changing a key’s field after "
            "insertion = lost entry."),

        h2("4.4 Concurrency Primitives"),
        table(
            ["Mechanism", "When to Use", "Key Point"],
            [
                ["synchronized", "Simple mutual exclusion", "Reentrant; intrinsic lock on object/class."],
                ["ReentrantLock", "Need tryLock, fairness, or interruptible lock", "Explicit lock/unlock in finally block."],
                ["volatile", "Flag/shared flag visibility", "Guarantees visibility, NOT atomicity."],
                ["AtomicInteger", "Lock-free counters", "CAS-based; no locking overhead."],
                ["StampedLock", "Read-heavy with occasional writes", "Optimistic read mode avoids write-blocking reads."],
            ],
            col_widths=[80, 170, 220]
        ),

        h2("4.5 ExecutorService &amp; CompletableFuture"),
        ans("Never use `new Thread()`. Use ExecutorService with a bounded pool. "
            "CompletableFuture gives non-blocking composition: thenApply, thenCompose, allOf, exceptionHandle. "
            "Always supply a custom Executor — the default ForkJoinPool is shared across your app."),
        warn("Rule of thumb: CPU-bound pool size = cores; IO-bound = much higher (e.g., 50–200)."),

        h2("4.6 Virtual Threads"),
        ans("Project Loom (Java 21+). Virtual threads are cheap — millions can run. Mounted on platform (carrier) "
            "threads. Ideal for IO-bound work (DB calls, HTTP). Don’t use for CPU-bound or with synchronized blocks "
            "(pins carrier). Use with Executors.newVirtualThreadPerTaskExecutor()."),

        h2("4.7 Streams &amp; Optional"),
        ans("Streams: declarative data pipelines. Use map/filter/reduce/collect. "
            "Avoid side-effects in lambda. Prefer method references. "
            "Optional: never return null; use orElse/orElseThrow. Never use Optional as a field or method arg."),

        h2("4.8 JVM Memory &amp; GC"),
        b("<b>Heap:</b> Young (Eden + S0/S1) → Old. <b>Stack:</b> per-thread, frames. "
          "<b>Metaspace:</b> class metadata, native memory."),
        b("<b>G1:</b> Default since Java 9. Region-based, predictable pause targets. Good for most workloads."),
        b("<b>ZGC:</b> Sub-millisecond pauses, concurrent compaction. Use for latency-critical services."),
        b("<b>High CPU?</b> Thread dump → look for tight loops or GC thrashing."),
        b("<b>Memory leak?</b> Heap dump → MAT/VisualVM → dominator tree → biggest retained objects."),
        b("<b>GC issues?</b> GC logs → check pause times, promotion failures, concurrent mode failures."),
    ]

def section_design_patterns():
    return [
        h1("5. Design Patterns — MUST PREPARE"),
        p("For each: <b>Pattern → When to use → Real example → Trade-off</b>."),
        sp(2),

        table(
            ["Pattern", "When / Why", "Example in Banking / Library", "Trade-off"],
            [
                ["Strategy",
                 "Interchangeable algorithms",
                 "FineCalculator with different rules per member type",
                 "More classes; but open for extension"],
                ["Factory Method",
                 "Create object without exposing which impl",
                 "PaymentProcessorFactory selects card/FPX/wire",
                 "Extra indirection; but decouples client"],
                ["Builder",
                 "Complex object, many optional fields",
                 "LoanDTO with optional fine, reservation ref",
                 "Verbose; but readable construction"],
                ["Observer",
                 "One-to-many event notification",
                 "LoanCreated event → fine check, email, audit",
                 "Debugging event chains can be hard"],
                ["Adapter",
                 "Incompatible interfaces",
                 "Legacy banking API wrapped to new internal interface",
                 "Extra layer; but protects domain from external churn"],
                ["Facade",
                 "Simplify complex subsystem",
                 "LibraryService as entry point over Loan+Fine+Reservation",
                 "Can become a god class if not careful"],
                ["Chain of Resp.",
                 "Pipeline of handlers",
                 "Validation chain: auth → input check → business rule",
                 "Ordering matters; debugging flow"],
                ["Decorator",
                 "Add behavior dynamically",
                 "LoggingDecorator around a PaymentService",
                 "Many small classes; hard to debug stack"],
                ["Template Method",
                 "Common skeleton, customizable steps",
                 "Base loan process; subclass overrides fine calc",
                 "Inheritance coupling; prefer Strategy for flexibility"],
                ["Singleton",
                 "Single instance",
                 "Spring beans are singleton-scoped by default",
                 "Global state; testability; use Spring’s container"],
            ],
            col_widths=[72, 105, 155, 138]
        ),
        sp(2),
        ans("<b>Practical question: ‘Three payment types with different logic.’</b> "
            "Use Strategy + Factory. Factory picks the correct Strategy bean based on payment type. "
            "Each strategy owns its own processing. Open/Closed: add a new payment type → add a new Strategy impl, "
            "no existing code changes."),
        warn("Also know: Repository, DI, MVC, CQRS, Saga, Outbox, Circuit Breaker, API Gateway — "
             "these are architectural patterns, not just GoF."),
    ]

def section_spring():
    return [
        h1("6. Spring Boot / JPA / Transactions"),

        h2("6.1 IoC / DI / Bean Lifecycle"),
        ans("Spring container creates, wires, and manages beans. Lifecycle: instantiation → "
            "@PostConstruct → business use → @PreDestroy. Scopes: singleton (default), prototype, "
            "request, session. Auto-configuration: @ConditionalOnClass/MissingBean → sensible defaults."),

        h2("6.2 AOP &amp; Proxy-Based @Transactional"),
        ans("Spring creates a CGLIB (or JDK dynamic) proxy around @Transactional beans. "
            "The proxy opens a transaction BEFORE the method call and commits/rolls back AFTER. "
            "This means <b>self-invocation bypasses the proxy</b> — a method calling another "
            "@Transactional method via `this` will NOT start a new transaction. Private/final methods "
            "also can’t be proxied."),
        warn("Self-invocation is the #1 reason @Transactional 'doesn't work' in interviews."),

        h2("6.3 Propagation &amp; Isolation"),
        table(
            ["Propagation", "Meaning"],
            [
                ["REQUIRED (default)", "Join existing tx; start new if none."],
                ["REQUIRES_NEW", "Always start a new tx; suspend outer."],
                ["NESTED", "Savepoint within outer tx; rollback to savepoint on error."],
                ["SUPPORTS / NOT_SUPPORTED / NEVER / MANDATORY", "Edge cases — know the names."],
            ],
            col_widths=[130, 340]
        ),
        b("<b>Isolation levels:</b> READ_UNCOMMITTED → READ_COMMITTED (default in PG) → "
          "REPEATABLE_READ → SERIALIZABLE. Higher = safer but slower. Dirty read → "
          "Non-repeatable read → Phantom read."),
        b("<b>Rollback:</b> Checked exceptions do NOT trigger rollback by default. "
          "Use rollbackFor = Exception.class to be safe."),

        h2("6.4 JPA: Lazy vs EAGER, N+1, Locking"),
        ans("<b>Lazy (default for @OneToMany):</b> loads on access. Must be within session/tx. "
            "<b>EAGER:</b> loads immediately. N+1: JPA issues 1 query for parent + N for children. "
            "Fix: JOIN FETCH in query, or @EntityGraph, or DTO projection with Spring Data Projections."),
        b("<b>Optimistic locking:</b> @Version field. On update, JPA checks version matches. "
          "If not → ObjectOptimisticLockingFailureException. No DB lock. Good for low contention."),
        b("<b>Pessimistic locking:</b> SELECT … FOR UPDATE. DB row lock. Good for high contention "
          "(e.g., borrowing the last available copy of a book)."),

        h2("6.5 REST Validation &amp; Exception Handling"),
        ans("@Valid + Bean Validation (@NotNull, @Size, @Pattern) on request DTOs. "
            "Global @ControllerAdvice + @ExceptionHandler catches all exceptions, returns "
            "consistent error response. Never expose stack traces to clients."),
    ]

def section_microservices():
    return [
        h1("7. Microservices + DDD"),

        h2("7.1 DDD Key Terms"),
        table(
            ["Term", "Definition"],
            [
                ["Domain", "The business problem space."],
                ["Subdomain", "A coherent area within the domain (Core, Supporting, Generic)."],
                ["Bounded Context", "Linguistic &amp; model boundary; same term, different meaning across contexts."],
                ["Entity", "Has identity that persists over time (e.g., Loan)."],
                ["Value Object", "No identity; defined by attributes (e.g., Money, Address)."],
                ["Aggregate", "Cluster of entities + value objects treated as one unit."],
                ["Aggregate Root", "The entry entity; external access only through it."],
                ["Domain Event", "Something that happened in the domain (e.g., LoanCreated)."],
                ["Repository", "Persistence abstraction; one per aggregate root."],
            ],
            col_widths=[100, 370]
        ),

        h2("7.2 Microservice Principles"),
        b("<b>Service boundaries:</b> Align with business capabilities, not technical layers."),
        b("<b>DB-per-service:</b> Each service owns its data. No direct DB access across services."),
        b("<b>Sync vs Async:</b> REST/gRPC for request-response. Kafka for domain events &amp; decoupling."),
        b("<b>Resilience:</b> Circuit breaker (stop calling failing service), timeout (fail fast), "
          "retry with backoff (transient errors), bulkhead (isolate resource pools)."),
        b("<b>Eventual consistency:</b> Across services, we accept temporary inconsistency. "
          "Sagas coordinate; outbox ensures reliable publishing."),

        h2("7.3 Saga: Orchestration vs Choreography"),
        ans("<b>Orchestration:</b> Central coordinator tells each service what to do. Easier to track, "
            "single point of failure. <b>Choreography:</b> Each service reacts to events. More decoupled, "
            "harder to trace. For banking: prefer orchestration — audit trail matters."),

        h2("7.4 Outbox Pattern"),
        ans("Problem: DB commit + Kafka publish must be atomic, but they aren’t. "
            "Solution: Write to an Outbox table in the SAME DB transaction. A separate relay "
            "(Debezium CDC or polling) reads the outbox and publishes to Kafka. Guaranteed: "
            "if the tx commits, the event WILL be published."),
    ]

def section_kafka():
    return [
        h1("8. Kafka / Event-Driven Architecture"),
        table(
            ["Concept", "Key Point"],
            [
                ["Topic / Partition", "Topic = logical stream; partition = ordered, parallel unit."],
                ["Offset", "Consumer’s position; committed periodically for at-least-once."],
                ["Consumer Group", "Each partition consumed by ONE consumer in a group. Scale = partition count."],
                ["Ordering", "Guaranteed WITHIN a partition only. Use partition key (e.g., accountId)."],
                ["At-least-once", "Default. Consumer commits after processing → crash before commit = reprocess."],
                ["Idempotent consumer", "Use idempotency key + DB unique constraint. Duplicate = no side effect."],
                ["Retry + DLQ", "Failed message → retry topic with backoff → after N retries → Dead Letter Topic."],
                ["Producer idempotence", "enable.idempotence=true; Kafka deduplicates on (PID, seq#)."],
                ["Transactions", "Consumer→process→produce + commit offset in one Kafka tx."],
            ],
            col_widths=[110, 360]
        ),
        ans("<b>Interview answer:</b> ‘We use Kafka for async domain events. Partition by aggregate ID "
            "for ordering. Consumers are idempotent via unique constraint on event ID. "
            "Failed messages go to a retry topic with exponential backoff, then DLQ for manual review. "
            "Outbox pattern ensures DB + Kafka atomicity.’"),
    ]

def section_db():
    return [
        h1("9. PostgreSQL + MongoDB"),

        h2("9.1 PostgreSQL"),
        b("<b>ACID:</b> Atomicity, Consistency, Isolation, Durability — the gold standard for financial data."),
        b("<b>Indexes:</b> B-tree (default), GIN (full-text), partial, composite. Composite index column order matters "
          "— most selective first, or match query patterns."),
        b("<b>EXPLAIN ANALYZE:</b> Always use this to verify query plans. Look for Seq Scan, index usage, join strategy."),
        b("<b>Connection pool:</b> HikariCP. Pool exhaustion = app hangs. Monitor active/idle/pending connections."),
        b("<b>Locking:</b> Row-level (FOR UPDATE), table-level, advisory locks. Deadlocks detected; transaction rolls back."),
        b("<b>Optimistic:</b> @Version column. Pessimistic: SELECT FOR UPDATE. Choose by contention level."),

        h2("9.2 MongoDB"),
        b("<b>Document model:</b> Embed for 1-to-few; reference for 1-to-many/many-to-many."),
        b("<b>Indexes:</b> Single, compound, text, geospatial. Covered queries = index only, no document fetch."),
        b("<b>Aggregation:</b> Pipeline stages: $match → $group → $sort → $project."),
        b("<b>Replica set:</b> Primary + secondaries. Auto-failover. Reads from secondary = eventual consistency."),
        b("<b>Sharding:</b> Horizontal scale. Shard key determines data distribution. Choose wisely — can’t change easily."),

        ans("<b>When to choose what:</b> PostgreSQL for strongly relational, transactional banking data "
            "(accounts, transfers, loans). MongoDB for document-oriented, flexible-schema domains "
            "(audit logs, config, analytics events). Often both coexist."),
    ]

def section_security():
    return [
        h1("10. Security — OWASP + Digital Banking"),
        b("<b>OWASP Top 10:</b> Broken Access Control, Crypto Failures, Injection, Insecure Design, "
          "Security Misconfiguration, Vulnerable Components, Auth Failures, Integrity Failures, "
          "Logging/Monitoring Failures, SSRF."),

        h2("Must-Know Security Points"),
        b("<b>HTTPS/TLS everywhere.</b> Mutual TLS for service-to-service in zero-trust architectures."),
        b("<b>OAuth2/OIDC:</b> Authorization Server issues JWT. Resource Server validates. "
          "Short-lived access tokens + refresh tokens. Rotate signing keys."),
        b("<b>AuthN ≠ AuthZ:</b> Authentication = who are you? Authorization = what can you do? "
          "Always enforce business-level authorization, not just role checks."),
        b("<b>Input validation:</b> Validate on boundary. Parameterized queries / JPA = no SQL injection."),
        b("<b>Never log:</b> passwords, tokens, secrets, unnecessary PII. Mask account numbers."),
        b("<b>Secrets management:</b> Kubernetes Secrets (base64, not encryption by default) or "
          "AWS Secrets Manager + IAM least-privilege policies."),
        b("<b>Rate limiting:</b> Protect APIs from abuse. Audit logs for every financial operation."),
        b("<b>Idempotency + transaction integrity:</b> Every money-movement request carries an "
          "idempotency key. DB unique constraint prevents double-processing. "
          "If in doubt, reject — never double-charge."),
    ]

def section_k8s():
    return [
        h1("11. Kubernetes / Docker / AWS / CI-CD"),

        h2("11.1 Kubernetes"),
        table(
            ["Concept", "Key Point"],
            [
                ["Pod", "Smallest deployable unit; 1+ containers sharing network/storage."],
                ["Deployment", "Declarative updates, replicas, rolling strategy."],
                ["Service", "Stable network endpoint; routes to pods via labels."],
                ["ConfigMap / Secret", "External config; Secret = base64-encoded (use external KMS for real encryption)."],
                ["Liveness probe", "Is the app healthy? Fails → restart pod."],
                ["Readiness probe", "Can the app serve traffic? Fails → remove from Service."],
                ["CrashLoopBackOff", "Check: kubectl describe pod, logs, previous-logs, events, probes, config."],
                ["OOMKilled", "Memory limit too low; JVM heap + native memory > limit. Set -Xmx < limit."],
            ],
            col_widths=[110, 360]
        ),

        h2("11.2 Docker Best Practices"),
        b("Multi-stage build: build in one image, copy JAR to slim runtime image."),
        b("Use distroless or eclipse-temurin-alpine. Run as non-root user."),
        b("Vulnerability scanning in CI (Trivy, Snyk). Pin base image digests."),

        h2("11.3 CI/CD Pipeline"),
        p("Build → Unit tests → SonarQube scan → Build Docker image → "
          "Push to ECR → Deploy to EKS (canary/blue-green) → Smoke tests → "
          "Promote or rollback."),

        h2("11.4 AWS Essentials"),
        b("<b>EKS:</b> Managed Kubernetes. <b>RDS:</b> Managed PostgreSQL. "
          "<b>ECR:</b> Container registry. <b>ALB:</b> Load balancer. "
          "<b>IAM:</b> Fine-grained roles. <b>CloudWatch:</b> Logs + metrics + alarms. "
          "<b>Secrets Manager:</b> Encrypted secrets with auto-rotation. "
          "<b>VPC:</b> Network isolation — public subnets (ALB), private (EKS, RDS)."),
    ]

def section_library():
    return [
        h1("12. Library Management System Assessment"),
        p("Requirements: books, copies, members, search, borrow, return, due dates, fines, reservations, availability."),
        sp(2),

        h2("Entity Model"),
        table(
            ["Entity", "Key Fields", "Notes"],
            [
                ["Book", "isbn, title, author", "The catalog entry (title). Not the physical copy."],
                ["BookCopy", "id, book_id, status", "Individually trackable physical copy. Status: AVAILABLE/BORROWED/LOST."],
                ["Member", "id, name, email, status", "ACTIVE/SUSPENDED."],
                ["Loan", "id, copy_id, member_id, borrowDate, dueDate, returnDate", "Borrowing record."],
                ["Reservation", "id, book_id, member_id, status", "WAITING/FULFILLED/CANCELLED."],
                ["Fine", "id, loan_id, amount, status", "UNPAID/PAID."],
            ],
            col_widths=[70, 200, 200]
        ),

        h2("API Endpoints"),
        b("POST /books — Add a book (and optionally copies)."),
        b("GET /books — Search books (title, author, ISBN)."),
        b("POST /members — Register a member."),
        b("POST /loans — Borrow a copy. <b>Lock the BookCopy row</b> (SELECT FOR UPDATE) "
          "to prevent double-borrow. Check status=AVAILABLE first."),
        b("POST /loans/{id}/return — Return a copy. Calculate fine if overdue. Update copy status."),
        b("POST /reservations — Reserve a book when no copy is available."),

        h2("Concurrency: The Critical Interview Point"),
        ans("Two members borrowing the same available copy: use @Transactional + "
            "pessimistic lock (SELECT FOR UPDATE on BookCopy row). This serializes access. "
            "Alternatively, optimistic locking with @Version — but under high contention, "
            "you’ll get many retry failures. For a library, pessimistic is simpler and correct."),
        warn("Lock the BookCopy row, NOT the Loan row. The invariant is on the copy’s availability."),

        h2("Pattern Choices"),
        b("<b>Strategy → FineCalculator:</b> Different fine rules per member type or loan period. "
          "Inject FineCalculator; each impl computes differently. Open for extension."),
        b("<b>Factory → NotificationSender:</b> Email vs SMS vs push. Factory picks the right sender."),
        b("<b>Repository → Persistence:</b> Spring Data JPA repositories. Abstraction over data access."),
        b("<b>Facade → LibraryService:</b> Single entry point coordinating Loan, Fine, Reservation. "
          "Keeps controllers thin."),
        tip("Don’t over-engineer. Explain WHY each pattern is needed. "
            "‘We use Strategy here because fine rules vary and will change — "
            "open/closed principle.’"),
    ]

def section_coding():
    return [
        h1("13. Coding Practice"),
        b("<b>HashMap frequency:</b> Iterate, count with map.getOrDefault(k,0)+1. O(n)."),
        b("<b>First non-repeating char:</b> Two-pass. First: count frequency. Second: find first with count 1. O(n)."),
        b("<b>Two Sum:</b> HashMap of value→index. For each element, check if complement exists. O(n)."),
        b("<b>Sliding window:</b> Max subarray of size k. Maintain window sum, slide, update max. O(n)."),
        b("<b>Merge intervals:</b> Sort by start. Iterate, merge if overlapping. O(n log n)."),
        b("<b>LRU Cache:</b> HashMap + doubly linked list. Get/Put in O(1). Evict tail on overflow."),
        b("<b>Thread-safe counter:</b> AtomicInteger.incrementAndGet(). Or LongAdder for high contention."),

        ans("<b>Coding flow:</b> Clarify requirements → Approach (name data structures) → "
            "Code → Test edge cases (null, empty, single element, duplicates) → "
            "State time/space complexity."),

        warn("For Library assessment coding: Borrow/Return with clean OOP. "
             "Show you think about concurrency and error handling, not just happy path."),
    ]

def section_production():
    return [
        h1("14. Production Scenarios"),

        h2("p99 doubled, CPU flat"),
        ans("Not CPU-bound. Check: DB response times, connection pool saturation, "
            "thread pool queue, lock contention, network latency, external API slowdown, "
            "Kafka consumer lag, GC pauses. Use APM traces (Jaeger/Datadog) to find the slow span."),

        h2("API returning 500s"),
        ans("Check logs for exceptions. Was there a recent deployment? Check downstream "
            "health (DB, Kafka, external APIs). Check metrics: error rate, latency, "
            "connection pool. Roll back if deployment-caused. Fix forward if known."),

        h2("Kafka consumer lag growing"),
        ans("Is consumer healthy? Check processing time per message → is a downstream "
            "call slow? Are partitions balanced? Scale consumers (can’t exceed partition count). "
            "Check for poison pill messages blocking the thread."),

        h2("DB CPU high"),
        ans("Find slow queries: pg_stat_statements, EXPLAIN ANALYZE. Missing indexes? "
            "Full table scans? Check connection count, lock waits, long-running transactions."),

        h2("Transaction not rolling back"),
        ans("1) Throwing a checked exception (default rollback is unchecked only). "
            "2) Self-invocation bypassing proxy. 3) Wrong propagation (SUPPORTS has no tx). "
            "4) @Transactional on a private method (proxy can’t intercept). Fix: rollbackFor = Exception.class."),

        h2("Pod restarts / OOMKilled"),
        ans("Check kubectl describe pod, events, previous logs. OOMKilled: JVM heap (-Xmx) "
            "too close to container memory limit. Rule: set -Xmx to ~70–80% of limit. "
            "Also check native memory (off-heap, NIO, metaspace)."),

        h2("Duplicate payment/event"),
        ans("Idempotency key on every request. DB unique constraint on (idempotency_key). "
            "If duplicate: return cached result, don’t reprocess. Consumer side: "
            "deduplicate by event ID + processed flag in DB."),
    ]

def section_leadership():
    return [
        h1("15. Leadership / Ownership"),
        b("<b>Mentoring:</b> Pair programming, weekly 1:1, code review as teaching moment. "
          "I create ADRs (Architecture Decision Records) so the team understands the ‘why’."),
        b("<b>Code/Design reviews:</b> Check correctness first, then readability, then patterns. "
          "I ask ‘what happens if this fails?’ rather than just style nitpicks."),
        b("<b>Architecture disagreements:</b> Write an ADR with options, trade-offs, and a recommendation. "
          "Data beats opinions. If still unresolved, time-box the decision and commit."),
        b("<b>Tech debt vs delivery:</b> Track debt as visible backlog items. Allocate 20% sprint capacity. "
          "Never let debt silently accumulate — make it visible, make it a choice."),
        b("<b>Communicating to non-tech:</b> Frame in business impact. ‘This change reduces "
          "incident response time by 50%’ beats ‘we’re adding circuit breakers.’"),
        b("<b>Production incident I owned:</b> Describe using STAR: Situation → Task → Action → Result. "
          "Emphasize: detected fast, mitigated, root-caused, prevented recurrence."),
        warn("Be honest: if you’re a technical lead (not people manager), say so. "
             "Mentoring ≠ managing. Both are leadership."),
    ]

def section_final_checklist():
    return [
        h1("16. Final 10-Minute Checklist"),
        p("Tick each item. If you can’t answer in 30 seconds, review that section."),
        sp(2),
        chk("HashMap internals &amp; HashMap vs ConcurrentHashMap"),
        chk("@Transactional: propagation, isolation, self-invocation pitfall"),
        chk("N+1 problem: cause, fix (JOIN FETCH / DTO projection)"),
        chk("Optimistic vs pessimistic locking"),
        chk("Design Patterns: Strategy, Factory, Builder, Adapter, Observer, Facade"),
        chk("DDD: bounded context, aggregate root, domain event"),
        chk("Kafka: consumer groups, partition ordering, idempotent consumer, DLQ"),
        chk("Outbox pattern + Saga (orchestration vs choreography)"),
        chk("PostgreSQL vs MongoDB: when to choose which"),
        chk("OWASP Top 3 + OAuth2/JWT flow + AuthN vs AuthZ"),
        chk("Kubernetes: liveness vs readiness, OOMKilled, CrashLoopBackOff"),
        chk("Library Management System: entities, borrow concurrency, pattern choices"),
        chk("One production incident story + one performance improvement story"),
        chk("90-second introduction rehearsed and timed"),
    ]

# ══════════════════════════════════════════════════════════
# BUILD PDF
# ══════════════════════════════════════════════════════════

def build():
    doc = SimpleDocTemplate(
        OUTPUT,
        pagesize=A4,
        leftMargin=18*mm, rightMargin=18*mm,
        topMargin=15*mm, bottomMargin=15*mm,
        title="AEON Interview Answer Key",
        author="Interview Prep"
    )

    sections = [
        section_cover,
        section_game_plan,
        section_intro,
        section_project_deep_dive,
        section_java,
        section_design_patterns,
        section_spring,
        section_microservices,
        section_kafka,
        section_db,
        section_security,
        section_k8s,
        section_library,
        section_coding,
        section_production,
        section_leadership,
        section_final_checklist,
    ]

    story = []
    for fn in sections:
        story.extend(fn())
        story.append(PageBreak())

    doc.build(story)
    print(f"PDF generated: {OUTPUT}")

if __name__ == "__main__":
    build()