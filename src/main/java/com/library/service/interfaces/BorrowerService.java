package com.library.service.interfaces;

import com.library.model.Borrower;

/**
 * Service interface for Borrower operations
 * Following standard best practices with separate interface and implementation
 */
public interface BorrowerService {

    /**
     * Register a new borrower
     * @param borrower Borrower entity to register
     * @return Registered borrower
     */
    Borrower register(Borrower borrower);

    /**
     * Get borrower by ID
     * @param id Borrower ID
     * @return Borrower entity
     */
    Borrower getById(Long id);
}