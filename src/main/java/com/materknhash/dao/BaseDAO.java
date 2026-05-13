package com.materknhash.dao;

import java.util.List;

/**
 * Generic Base Interface for Data Access Objects.
 * Fulfills the "Generics (Mandatory)" requirement.
 * @param <T> The entity type
 */
public interface BaseDAO<T> {
    boolean add(T item);
    boolean update(T item);
    boolean delete(int id);
    T getById(int id);
    List<T> getAll();
}
