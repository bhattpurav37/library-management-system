package com.library.service;

import com.library.exception.IsbnConflictException;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @InjectMocks
    private com.library.service.BookServiceImpl bookService;

    private final String isbn = "9780132350884";

    @Test
    void register_createsNewCopyWhenIsbnIsNew() {
        when(bookRepository.findByIsbn(isbn)).thenReturn(List.of());
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book book = new Book(isbn, "Clean Code", "Robert C. Martin");
        Book saved = bookService.register(book);

        assertThat(saved.getIsbn()).isEqualTo(isbn);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void register_allowsSecondCopyOfSameBook() {
        Book existing = new Book(isbn, "Clean Code", "Robert C. Martin");
        existing.setId(1L);
        when(bookRepository.findByIsbn(isbn)).thenReturn(List.of(existing));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book book = new Book(isbn, "Clean Code", "Robert C. Martin");
        Book saved = bookService.register(book);

        assertThat(saved.getIsbn()).isEqualTo(isbn);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void register_rejectsSameIsbnWithDifferentTitle() {
        Book existing = new Book(isbn, "Clean Code", "Robert C. Martin");
        existing.setId(1L);
        when(bookRepository.findByIsbn(isbn)).thenReturn(List.of(existing));

        Book book = new Book(isbn, "A Different Book", "Robert C. Martin");
        assertThatThrownBy(() -> bookService.register(book))
                .isInstanceOf(IsbnConflictException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void isAvailable_trueWhenNoActiveLoan() {
        when(borrowRecordRepository.findActiveLoanForBook(1L)).thenReturn(Optional.empty());

        assertThat(bookService.isAvailable(1L)).isTrue();
    }
}
