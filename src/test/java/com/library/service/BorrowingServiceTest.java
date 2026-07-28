package com.library.service;

import com.library.exception.BookAlreadyBorrowedException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Borrower;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BorrowerRepository borrowerRepository;
    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @InjectMocks
    private BorrowingServiceImpl borrowingService;

    private Book book;
    private Borrower borrower;

    @BeforeEach
    void setUp() {
        book = new Book("9780132350884", "Clean Code", "Robert C. Martin");
        book.setId(1L);
        borrower = new Borrower("Ada Lovelace", "ada@example.com");
        borrower.setId(1L);
    }

    @Test
    void borrow_succeedsWhenBookIsFree() {
        when(bookRepository.findForUpdate(1L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(borrowRecordRepository.findActiveLoanForBookForUpdate(1L)).thenReturn(Optional.empty());
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        BorrowRecord result = borrowingService.borrow(1L, 1L);

        assertThat(result.getBook()).isEqualTo(book);
        assertThat(result.getBorrower()).isEqualTo(borrower);
        assertThat(result.isActive()).isTrue();
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
    }

    @Test
    void borrow_throwsWhenBookAlreadyOnLoan() {
        when(bookRepository.findForUpdate(1L)).thenReturn(Optional.of(book));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(borrowRecordRepository.findActiveLoanForBookForUpdate(1L))
                .thenReturn(Optional.of(new BorrowRecord(book, borrower)));

        assertThatThrownBy(() -> borrowingService.borrow(1L, 1L))
                .isInstanceOf(BookAlreadyBorrowedException.class);

        verify(borrowRecordRepository, never()).save(any());
    }

    @Test
    void borrow_throwsWhenBookDoesNotExist() {
        when(bookRepository.findForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowingService.borrow(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("book 99");
    }

    @Test
    void returnBook_marksActiveLoanAsReturned() {
        BorrowRecord activeLoan = new BorrowRecord(book, borrower);
        when(borrowRecordRepository.findActiveLoanForBookAndBorrower(1L, 1L)).thenReturn(Optional.of(activeLoan));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        BorrowRecord result = borrowingService.returnBook(1L, 1L);

        assertThat(result.isActive()).isFalse();
        assertThat(result.getReturnedAt()).isNotNull();
    }

    @Test
    void returnBook_throwsWhenNoActiveLoanExists() {
        when(borrowRecordRepository.findActiveLoanForBookAndBorrower(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowingService.returnBook(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
