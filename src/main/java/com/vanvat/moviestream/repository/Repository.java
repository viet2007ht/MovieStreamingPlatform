package com.vanvat.moviestream.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD contract every file-backed repository implements.
 * Keeping this as an interface (OOP: abstraction/polymorphism) lets the
 * service layer depend on behavior, not on file details.
 */
public interface Repository<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T entity);      // insert or update
    void deleteById(ID id);
    boolean existsById(ID id);
}
