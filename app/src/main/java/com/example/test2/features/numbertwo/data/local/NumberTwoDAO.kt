package com.example.test2.features.numbertwo.data.local

import com.example.test2.data.dao.behaviors.TodoLineableDAO
import io.objectbox.Box

/**
 * Provides persistence operations for {@link NumberTwoEntity}.
 *
 * Implementations are responsible for managing storage, retrieval and deletion
 * of NumberTwo entities.
 */
interface NumberTwoDAO : TodoLineableDAO<NumberTwoEntity> {


    /**
     * Initialises the DAO with the backing ObjectBox storage.
     *
     * This method must be invoked before any other operation is performed.
     *
     * @param box the ObjectBox container for NumberTwo entities.
     */
    fun initialize(box: Box<NumberTwoEntity>)
    fun getBox(): Box<NumberTwoEntity>

    /**
     * Persists the supplied entity.
     *
     * @param numberTwoEntity the entity to be stored.
     * @return the database identifier assigned to the entity.
     * @throws Exception if the entity cannot be stored.
     */
    fun insert(numberTwoEntity: NumberTwoEntity): Long



    /**
     * Retrieves a paginated collection of entities.
     *
     * Results are expected to be ordered according to the implementation
     * strategy.
     *
     * @param offset zero-based starting position.
     * @param limit maximum number of records to retrieve.
     * @return a list of entities.
     */
    fun getNumberTwoList(
        offset: Long,
        limit: Long
    ): List<NumberTwoEntity>

    /**
     * Removes every stored entity.
     */
    fun deleteAll()

    /**
     * Retrieves all stored entities.
     *
     * @return all persisted entities.
     */
    fun getAll(): List<NumberTwoEntity>

    /**
     * Removes the supplied entity.
     *
     * @param todoLineable the entity to remove.
     * @return {@code true} if an entity was removed; otherwise {@code false}.
     */
    override fun delete(todoLineable: NumberTwoEntity): Boolean

}
