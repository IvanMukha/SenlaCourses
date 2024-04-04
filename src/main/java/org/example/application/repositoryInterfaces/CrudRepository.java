package org.example.application.repositoryInterfaces;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {

    List<T> getAll();

    T save(T entity);

    Optional<T> getById(int id);

    Optional<T> update(int id, T updatedEntity);

    void delete(int id);
}
