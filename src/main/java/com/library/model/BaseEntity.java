package com.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Base entity class containing common fields for all entities.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime lastModifiedDate;

    @Column(name = "version")
    @Version
    @JsonIgnore
    private Long version;
}
