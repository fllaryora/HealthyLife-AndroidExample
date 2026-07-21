package com.example.test2.features.recordpill.data.local


import io.objectbox.Box
import com.example.test2.data.dao.behaviors.TodoLineableDAO
import com.example.test2.features.pill.data.local.PillEntity

/**
 * Data Access Object contract for managing {@link PillTakenEntity} persistence.
 *
 * <p>
 * Implementations are responsible for storing, retrieving and deleting
 * pill intake records associated with a specific {@link PillEntity}.
 * </p>
 *
 * <p>
 * This abstraction exists to decouple higher layers from the underlying
 * persistence framework (ObjectBox, Room, etc.).
 * </p>
 */
interface PillTakenDAO : TodoLineableDAO<PillTakenEntity> {

    /**
     * Initializes the DAO with the ObjectBox storage instance.
     *
     * This method must be called before performing any CRUD operation.
     *
     * @param box ObjectBox box used to store and retrieve [PillTakenEntity] instances.
     */

    fun initialize(box: Box<PillTakenEntity>) : Unit

    /**
     * Persists a pill intake record.
     *
     * @param pillTakenEntity entity to insert.
     * @return generated identifier of the inserted entity.
     */
    fun insert(pillTakenEntity: PillTakenEntity): Long

    /**
     * Deletes all intake records associated with a specific pill.
     *
     * @param pillEntity pill whose intake history should be removed.
     */
    fun deleteByPill(pillEntity: PillEntity)

    /**
     * Returns a paginated list of pill intake records together with the
     * first intake time converted to decimal hours.
     *
     * <p>
     * The returned pair contains:
     * </p>
     * <ul>
     *     <li>First value: the requested page of {@link PillTakenEntity} records.</li>
     *     <li>Second value: the first intake time expressed in decimal hours,
     *     or null when no records exist.</li>
     * </ul>
     *
     * @param pillEntity pill to query.
     * @param offset starting position.
     * @param limit maximum number of records to return.
     * @return pair containing intake records and first intake hour.
     */
    fun getPillTaken(
        pillEntity: PillEntity,
        offset: Long,
        limit: Long
    ): Pair<List<PillTakenEntity>, Float?>

    /**
     * Returns a paginated list of intake records ordered chronologically.
     *
     * @param pillEntity pill to query.
     * @param offset starting position.
     * @param limit maximum number of records to return.
     * @return list of intake records.
     */
    fun getPillTakenList(
        pillEntity: PillEntity,
        offset: Long,
        limit: Long
    ): List<PillTakenEntity>

    /**
     * Removes all stored intake records.
     */
    fun deleteAll()

    /**
     * Returns every intake record associated with the given pill.
     *
     * @param pillEntity pill to query.
     * @return all matching intake records.
     */
    fun getAllByPill(
        pillEntity: PillEntity
    ): List<PillTakenEntity>

    /**
     * Returns all stored intake records.
     *
     * @return complete list of pill intake records.
     */
    fun getAll(): List<PillTakenEntity>

    fun getBox(): Box<PillTakenEntity>
}
