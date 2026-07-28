package com.library.service.interfaces;

import com.library.model.BorrowRecord;

/**
 * Service interface for Borrowing operations
 * Following standard best practices with separate interface and implementation
 */
public interface BorrowingService {

    /**
     * Borrow a book
     * @param bookId Book ID to borrow
     * @param borrowerId Borrower ID to borrow the book
     * @return BorrowRecord entity for the loan
     */
    BorrowRecord borrow(Long bookId, Long borrowerId);

    /**
     * Return a book
     * @param bookId Book ID to return
     * @param borrowerId Borrower ID returning the book
     * @return Updated BorrowRecord with return timestamp
     */
    BorrowRecord returnBook(Long bookId, Long borrowerId);
}