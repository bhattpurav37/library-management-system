package com.library.service;

import com.library.exception.IsbnConflictException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.service.interfaces.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service("bookService")
public class BookServiceImpl implements com.library.service.interfaces.BookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public BookServiceImpl(BookRepository bookRepository, BorrowRecordRepository borrowRecordRepository) {
        this.bookRepository = bookRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @Override
    @Transactional
    public Book register(Book book) {
        String isbn = book.getIsbn();
        String title = book.getTitle();
        String author = book.getAuthor();

        // Rule from the spec: two books sharing an ISBN must have the same title/author.
        // We only need to check against one existing copy since this is enforced on every write.
        List<Book> existingCopies = bookRepository.findByIsbn(isbn);
        if (!existingCopies.isEmpty()) {
            Book existing = existingCopies.get(0);
            boolean titleMatches = existing.getTitle().equalsIgnoreCase(title);
            boolean authorMatches = existing.getAuthor().equalsIgnoreCase(author);
            if (!titleMatches || !authorMatches) {
                LOGGER.error(String.format("ISBN conflict: isbn [%s] is already registered with title [%s] and author [%s] - cannot be reused for a different book",
                        isbn, existing.getTitle(), existing.getAuthor()));
                throw new IsbnConflictException(
                        "isbn " + isbn + " is already registered with title '" + existing.getTitle()
                                + "' and author '" + existing.getAuthor() + "' - it cannot be reused for a different book");
            }
            LOGGER.info(String.format("Registering additional copy for isbn: [%s]", isbn));
        }

        Book saved = bookRepository.save(book);
        LOGGER.info(String.format("Book registered successfully with id: [%s], isbn: [%s]", saved.getId(), isbn));
        return saved;
    }

    @Override
    public List<Book> getAll() {
        LOGGER.info(String.format("Fetching all books"));
        return bookRepository.findAll();
    }

    @Override
    public Book getById(Long id) {
        LOGGER.info(String.format("Fetching book by id: [%s]", id));
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("book " + id + " not found"));
    }

    @Override
    public boolean isAvailable(Long bookId) {
        LOGGER.info(String.format("Checking availability for book id: [%s]", bookId));
        return borrowRecordRepository.findActiveLoanForBook(bookId).isEmpty();
    }

    @Override
    public Set<Long> findBorrowedBookIds() {
        return borrowRecordRepository.findBookIdsWithActiveLoan();
    }
}
