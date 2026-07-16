package com.example.test2.data.dao.implementations

import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.dao.behaviors.TodoLineableDAO
import com.example.test2.data.entities.implementations.Pill
import com.example.test2.data.entities.implementations.PillTaken
import com.example.test2.data.entities.implementations.PillTaken_
import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

/**
 * This class is absolutely Framework dependent.
 * DAO for objectbox
 */
object PillTakenDAO : TodoLineableDAO<PillTaken> {
    private lateinit var mPillTakenBox: Box<PillTaken>

    fun initialize(box: Box<PillTaken>) {
        mPillTakenBox = box
    }

    private fun getPillTakenQuery(pill: Pill): Query<PillTaken> {
        return mPillTakenBox.query()
            .equal(PillTaken_.pillId, pill.id)
            .order(
                PillTaken_.date, QueryBuilder.DESCENDING
                        or QueryBuilder.CASE_SENSITIVE  ).build()
    }


    fun insert(pillTaken: PillTaken) : Long {
        //return the new key
        return mPillTakenBox.put(pillTaken)
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: PillTaken) : Boolean {
        return mPillTakenBox.remove(todoLineable)
    }

    fun deleteByPill(pill: Pill) {
        val list: List<PillTaken> = getPillTakenQuery(pill).find()
        mPillTakenBox.remove(list)
    }

    fun getPillTaken(pill: Pill, offset: Long, limit: Long): Pair<List<PillTaken>, Float?> {
        val list: List<PillTaken> = getPillTakenList(pill,offset,limit)
        val firstTake: Float? = TimeConverter.convertISOToHours(list.firstOrNull()?.getTime())
        return Pair(list, firstTake)
    }

    fun getPillTakenList(pill: Pill, offset: Long, limit: Long): List<PillTaken> {
        val list: List<PillTaken> = getPillTakenQuery(pill).find(offset,limit)
        return list.sortedBy { pillTaken: PillTaken -> pillTaken.date } //ascending
    }

    fun deleteAll() {
        return mPillTakenBox.removeAll()
    }

    fun getAllByPill(pill: Pill): List<PillTaken> {
        return getPillTakenQuery(pill).find()
    }

    fun getAll(): List<PillTaken> {
        return mPillTakenBox.query().build().find()
    }
}