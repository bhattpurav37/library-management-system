package com.library.service;

import com.library.exception.ResourceNotFoundException;
import com.library.model.Borrower;
import com.library.repository.BorrowerRepository;
import com.library.service.interfaces.BorrowerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("borrowerService")
public class BorrowerServiceImpl implements com.library.service.interfaces.BorrowerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowerServiceImpl.class);

    private final BorrowerRepository borrowerRepository;

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    @Transactional
    public Borrower register(Borrower borrower) {
        borrowerRepository.findByEmailIgnoreCase(borrower.getEmail()).ifPresent(b -> {
            LOGGER.error(String.format("Duplicate borrower email: [%s]", borrower.getEmail()));
            throw new IllegalArgumentException("a borrower with email " + borrower.getEmail() + " is already registered");
        });
        Borrower saved = borrowerRepository.save(borrower);
        LOGGER.info(String.format("Borrower registered successfully with id: [%s], email: [%s]", saved.getId(), saved.getEmail()));
        return saved;
    }

    @Override
    public Borrower getById(Long id) {
        LOGGER.info(String.format("Fetching borrower by id: [%s]", id));
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("borrower " + id + " not found"));
    }
}
