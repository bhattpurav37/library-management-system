package com.library.repository;

import com.library.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from BorrowRecord r where r.book.id = :bookId and r.returnedAt is null")
    Optional<BorrowRecord> findActiveLoanForBookForUpdate(Long bookId);

    @Query("select r from BorrowRecord r where r.book.id = :bookId and r.borrower.id = :borrowerId and r.returnedAt is null")
    Optional<BorrowRecord> findActiveLoanForBookAndBorrower(Long bookId, Long borrowerId);

    @Query("select r from BorrowRecord r where r.book.id = :bookId and r.returnedAt is null")
    Optional<BorrowRecord> findActiveLoanForBook(Long bookId);

    /**
     * Returns the ids of every book copy that currently has an active (unreturned) loan. Used to
     * compute the per-copy availability flag for the book listing in a single query instead of one
     * query per book.
     */
    @Query("select r.book.id from BorrowRecord r where r.returnedAt is null")
    java.util.Set<Long> findBookIdsWithActiveLoan();
}
