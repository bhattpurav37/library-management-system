# Assumptions

Decisions made where the task didn't specify details.

- **Book `id` vs ISBN.** `id` identifies one physical copy; `isbn` identifies the
  work. Multiple copies share an ISBN but get different ids.
- **ISBN format.** Accepted as 10 or 13 digits (with or without hyphens), no
  checksum validation. The task's concern is uniqueness, not ISBN correctness.
- **Borrower email is unique.** Registering the same email twice returns `400`.
- **No update/delete endpoints.** Only the actions listed in the task are
  implemented.
- **One active loan per copy (`id`), not per title.** Two different copies of the
  same book can be borrowed at the same time.
- **Return requires the same borrower who borrowed it.** A different borrower
  can't return a book they didn't borrow — returns `404`.
- **No due dates, fines, or reservations.** Not in the task. `borrowedAt` and
  `returnedAt` are recorded so these could be added later.
- **No borrowing limit.** A borrower can hold multiple books at once.
- **No authentication.** Out of scope for the task.
- **Prod uses `ddl-auto: update`.** A migration tool (Flyway/Liquibase) would be
  used in a real production setup.
