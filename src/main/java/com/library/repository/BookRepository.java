package com.library.repository;

import com.library.model.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByIsbn(String isbn);

    /**
     * Loads a book copy with a pessimistic write lock (SELECT ... FOR UPDATE). Used by the borrow
     * flow so two concurrent borrows of the same copy serialize on the book row itself - the active-
     * loan lookup alone locks nothing when the copy is free, which would let both requests through.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id = :id")
    Optional<Book> findForUpdate(@Param("id") Long id);
}
