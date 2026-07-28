package com.library.service.interfaces;

import com.library.model.Book;
import java.util.List;
import java.util.Set;

/**
 * Service interface for Book operations
 * Following standard best practices with separate interface and implementation
 */
public interface BookService {

    /**
     * Register a new book
     * @param book Book entity to register
     * @return Registered book
     */
    Book register(Book book);

    /**
     * Get all books
     * @return List of all books
     */
    List<Book> getAll();

    /**
     * Get book by ID
     * @param id Book ID
     * @return Book entity
     */
    Book getById(Long id);

    /**
     * Check if a book is available for borrowing
     * @param bookId Book ID
     * @return true if available, false otherwise
     */
    boolean isAvailable(Long id);

    /**
     * Returns the ids of every book copy that is currently on an active loan, in a single query.
     * Used to compute per-copy availability for the book listing without an N+1.
     * @return set of book ids that are currently unavailable
     */
    Set<Long> findBorrowedBookIds();
}
