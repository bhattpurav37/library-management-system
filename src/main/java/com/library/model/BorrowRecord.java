package com.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents one loan of a book copy to a borrower. While returnedAt is null
 * the loan is considered active, i.e. the book is currently out.
 */
@Entity
@Table(name = "borrow_records")
@Getter
@Setter
@NoArgsConstructor
public class BorrowRecord extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private Borrower borrower;

    @Column(nullable = false)
    private LocalDateTime borrowedAt;

    private LocalDateTime returnedAt;

    public BorrowRecord(Book book, Borrower borrower) {
        this.book = book;
        this.borrower = borrower;
        this.borrowedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return returnedAt == null;
    }
}
