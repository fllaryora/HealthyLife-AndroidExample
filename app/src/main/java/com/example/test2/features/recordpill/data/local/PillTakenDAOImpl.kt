package com.example.test2.features.recordpill.data.local

import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.dao.behaviors.TodoLineableDAO
import com.example.test2.features.pill.data.local.PillEntity
import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

/**
 * This class is absolutely Framework dependent.
 * DAO for objectbox
 */
object PillTakenDAOImpl : TodoLineableDAO<PillTakenEntity> {
    private lateinit var mPillTakenEntityBox: Box<PillTakenEntity>

    fun initialize(box: Box<PillTakenEntity>) {
        mPillTakenEntityBox = box
    }

    private fun getPillTakenQuery(pillEntity: PillEntity): Query<PillTakenEntity> {
        return mPillTakenEntityBox.query()
            .equal(PillTakenEntity_.pillEntityId, pillEntity.id)
            .order(
                PillTakenEntity_.date, QueryBuilder.DESCENDING
                        or QueryBuilder.CASE_SENSITIVE  ).build()
    }


    fun insert(pillTakenEntity: PillTakenEntity) : Long {
        //return the new key
        return mPillTakenEntityBox.put(pillTakenEntity)
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: PillTakenEntity) : Boolean {
        return mPillTakenEntityBox.remove(todoLineable)
    }

    fun deleteByPill(pillEntity: PillEntity) {
        val list: List<PillTakenEntity> = getPillTakenQuery(pillEntity).find()
        mPillTakenEntityBox.remove(list)
    }

    fun getPillTaken(pillEntity: PillEntity, offset: Long, limit: Long): Pair<List<PillTakenEntity>, Float?> {
        val list: List<PillTakenEntity> = getPillTakenList(pillEntity,offset,limit)
        val firstTake: Float? = TimeConverter.convertISOToHours(list.firstOrNull()?.getTime())
        return Pair(list, firstTake)
    }

    fun getPillTakenList(pillEntity: PillEntity, offset: Long, limit: Long): List<PillTakenEntity> {
        val list: List<PillTakenEntity> = getPillTakenQuery(pillEntity).find(offset,limit)
        return list.sortedBy { pillTakenEntity: PillTakenEntity -> pillTakenEntity.date } //ascending
    }

    fun deleteAll() {
        return mPillTakenEntityBox.removeAll()
    }

    fun getAllByPill(pillEntity: PillEntity): List<PillTakenEntity> {
        return getPillTakenQuery(pillEntity).find()
    }

    fun getAll(): List<PillTakenEntity> {
        return mPillTakenEntityBox.query().build().find()
    }
}