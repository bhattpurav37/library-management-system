package com.library.service;

import com.library.exception.BookAlreadyBorrowedException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.BorrowerRepository;
import com.library.service.interfaces.BorrowingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service("borrowingService")
public class BorrowingServiceImpl implements com.library.service.interfaces.BorrowingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowingServiceImpl.class);

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public BorrowingServiceImpl(BookRepository bookRepository,
                             BorrowerRepository borrowerRepository,
                             BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    /**
     * Borrows a book on behalf of a borrower.
     * <p>
     * The book copy is loaded with a pessimistic write lock ({@code SELECT ... FOR UPDATE}) so two
     * concurrent borrows of the same copy serialize on the book row itself: the second request
     * blocks until the first transaction commits, then its active-loan check sees the freshly
     * inserted loan and is rejected. Locking the loan row would not be enough on its own - when the
     * copy is free the active-loan lookup returns no rows and locks nothing (Postgres has no gap
     * locking), so both requests would slip through. That ordering is what actually satisfies
     * requirement 8 under concurrent load.
     */
    @Override
    @Transactional
    public BorrowRecord borrow(Long bookId, Long borrowerId) {
        LOGGER.info(String.format("Borrowing book id: [%s] for borrower id: [%s]", bookId, borrowerId));

        Book book = bookRepository.findForUpdate(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("book " + bookId + " not found"));
        Borrower borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new ResourceNotFoundException("borrower " + borrowerId + " not found"));

        borrowRecordRepository.findActiveLoanForBookForUpdate(bookId).ifPresent(r -> {
            LOGGER.error(String.format("Book already borrowed: book id: [%s] is already on loan", bookId));
            throw new BookAlreadyBorrowedException("book " + bookId + " is already on loan");
        });

        BorrowRecord record = new BorrowRecord(book, borrower);
        BorrowRecord saved = borrowRecordRepository.save(record);
        LOGGER.info(String.format("Book borrowed successfully: book id: [%s], borrower id: [%s], borrow record id: [%s]",
                bookId, borrowerId, saved.getId()));
        return saved;
    }

    @Override
    @Transactional
    public BorrowRecord returnBook(Long bookId, Long borrowerId) {
        LOGGER.info(String.format("Returning book id: [%s] for borrower id: [%s]", bookId, borrowerId));

        BorrowRecord record = borrowRecordRepository.findActiveLoanForBookAndBorrower(bookId, borrowerId)
                .orElseThrow(() -> {
                    LOGGER.error(String.format("No active loan found for book id: [%s] and borrower id: [%s]", bookId, borrowerId));
                    return new ResourceNotFoundException(
                            "no active loan found for book " + bookId + " and borrower " + borrowerId);
                });

        record.setReturnedAt(LocalDateTime.now());
        BorrowRecord saved = borrowRecordRepository.save(record);
        LOGGER.info(String.format("Book returned successfully: book id: [%s], borrower id: [%s], borrow record id: [%s]",
                bookId, borrowerId, saved.getId()));
        return saved;
    }
}
