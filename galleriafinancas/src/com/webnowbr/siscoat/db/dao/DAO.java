package com.webnowbr.siscoat.db.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Data Access Object interface. Contains methods for CRUD operations.
 * @author domingos
 * @param <T> DAO Data type.
 * @param <ID> DAO id type.
 */
public interface DAO<T, ID extends Serializable> {

    /**
     * Persists the instance object into database.
     * @param instance The instance to persist.
     * @return the id of the stored instance.
     */
    ID create(T instance);

    /**
     * Finds an instance based on its id.
     * @param id The instance id.
     * @return T The found instance.
     */
    T findById(ID id);

    /**
     * Finds all instances of the data type.
     * @return List<T> List of found instances.
     */
    List<T> findAll();

    /**
     * Updates an entity.
     * @param entity The entity to be updated.
     */
    void update(T entity);

    /**
     * Deletes an entity.
     * @param entity The entity to be deleted.
     */
    void delete(T entity);
}
