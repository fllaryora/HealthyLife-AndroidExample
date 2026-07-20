package com.example.test2.features.pill.data.local

import io.objectbox.Box

/**
 * Data Access Object (DAO) for managing [PillEntity] persistence operations.
 *
 * Provides basic CRUD operations for pills stored in the local database.
 */
interface PillDAO {

    /**
     * Inserts or updates a pill entity.
     *
     * @param pillEntity The pill entity to be stored.
     * @return The ID of the stored entity.
     * @throws Exception If the operation violates a unique constraint.
     */
    fun insert(pillEntity: PillEntity): Long

    /**
     * Removes the specified pill entity.
     *
     * @param pillEntity The entity to delete.
     * @return `true` if the entity was successfully removed,
     * `false` if no entity exists with the given ID.
     */
    fun delete(pillEntity: PillEntity): Boolean

    /**
     * Retrieves all stored pill entities.
     *
     * @return A list containing all pills.
     */
    fun getPills(): List<PillEntity>

    /**
     * Removes all pill entities from storage.
     */
    fun deleteAll()

    fun initialize(box: Box<PillEntity>)

    fun getBox() : Box<PillEntity>
}