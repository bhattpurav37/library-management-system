package com.library.service;

import com.library.exception.BookAlreadyBorrowedException;
import com.library.model.Book;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;
import com.library.service.interfaces.BorrowingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves requirement 8 holds under concurrent load: two borrowers trying to borrow the same free
 * book copy at the same time must result in exactly one success and one rejection. Runs against a
 * real H2 database (not mocks) so the pessimistic lock actually executes.
 * <p>
 * The setup rows are persisted on the test thread with no surrounding transaction, so each spawned
 * borrow thread sees them. Each borrow call runs in its own service-managed transaction.
 */
@SpringBootTest
@ActiveProfiles("test")
class BorrowingConcurrencyIT {

    @Autowired
    private BorrowingService borrowingService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Test
    void concurrentBorrowOfSameFreeCopy_onlyOneSucceeds() throws Exception {
        Book book = bookRepository.save(new Book("9780596007126", "Head First Design Patterns", "Eric Freeman"));
        Borrower borrowerA = borrowerRepository.save(new Borrower("Ada Lovelace", "ada@example.com"));
        Borrower borrowerB = borrowerRepository.save(new Borrower("Alan Turing", "alan@example.com"));

        Long bookId = book.getId();

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(2);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger conflicts = new AtomicInteger();
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        Runnable taskForA = () -> borrowAndTally(borrowerA.getId(), bookId, startGate, doneGate, successes, conflicts, errors);
        Runnable taskForB = () -> borrowAndTally(borrowerB.getId(), bookId, startGate, doneGate, successes, conflicts, errors);

        Thread t1 = new Thread(taskForA, "borrower-A");
        Thread t2 = new Thread(taskForB, "borrower-B");
        t1.start();
        t2.start();

        // Release both threads at the same instant to maximise the chance of a true race.
        startGate.countDown();
        doneGate.await();
        t1.join();
        t2.join();

        assertThat(errors).isEmpty();
        assertThat(successes.get()).as("exactly one borrow should succeed").isEqualTo(1);
        assertThat(conflicts.get()).as("exactly one borrow should be rejected as a conflict").isEqualTo(1);
    }

    private void borrowAndTally(Long borrowerId, Long bookId, CountDownLatch startGate, CountDownLatch doneGate,
                                AtomicInteger successes, AtomicInteger conflicts, List<Throwable> errors) {
        try {
            startGate.await();
            borrowingService.borrow(bookId, borrowerId);
            successes.incrementAndGet();
        } catch (BookAlreadyBorrowedException ex) {
            conflicts.incrementAndGet();
        } catch (Throwable t) {
            errors.add(t);
        } finally {
            doneGate.countDown();
        }
    }
}
