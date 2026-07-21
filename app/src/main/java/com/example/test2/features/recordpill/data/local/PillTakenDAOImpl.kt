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
object PillTakenDAOImpl : PillTakenDAO {
    private lateinit var mPillTakenEntityBox: Box<PillTakenEntity>

    override fun initialize(box: Box<PillTakenEntity>) {
        mPillTakenEntityBox = box
    }

    override fun getBox() : Box<PillTakenEntity>{
        return mPillTakenEntityBox
    }

    private fun getPillTakenQuery(pillEntity: PillEntity): Query<PillTakenEntity> {
        return mPillTakenEntityBox.query()
            .equal(PillTakenEntity_.pillEntityId, pillEntity.id)
            .order(
                PillTakenEntity_.date, QueryBuilder.DESCENDING
                        or QueryBuilder.CASE_SENSITIVE  ).build()
    }


    override fun insert(pillTakenEntity: PillTakenEntity) : Long {

        check(pillTakenEntity.pillEntity.targetId != 0L) {
            """
        PillTakenEntity.pillEntity relation is missing.

        exportPillId=${pillTakenEntity.exportPillId}
        targetId=${pillTakenEntity.pillEntity.targetId}

        Setting exportPillId does NOT initialize the ObjectBox relation.
        Use:

        pillTakenEntity.pillEntity.target = pillEntity

        or

        pillTakenEntity.pillEntity.targetId = pillEntity.id
        """.trimIndent()
        }

        //return the new key
        return mPillTakenEntityBox.put(pillTakenEntity)
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: PillTakenEntity) : Boolean {

        check(todoLineable.pillEntity.targetId != 0L) {
            """
        PillTakenEntity.pillEntity relation is missing.

        exportPillId=${todoLineable.exportPillId}
        targetId=${todoLineable.pillEntity.targetId}

        Setting exportPillId does NOT initialize the ObjectBox relation.
        Use:

        pillTakenEntity.pillEntity.target = pillEntity

        or

        pillTakenEntity.pillEntity.targetId = pillEntity.id
        """.trimIndent()
        }

        return mPillTakenEntityBox.remove(todoLineable)
    }

    override fun deleteByPill(pillEntity: PillEntity) {
        val list: List<PillTakenEntity> = getPillTakenQuery(pillEntity).find()
        if(list.isNotEmpty()) {
            mPillTakenEntityBox.remove(list)
        } else {
            println("The list is empty ${list}")
        }

    }

    override fun getPillTaken(pillEntity: PillEntity, offset: Long, limit: Long): Pair<List<PillTakenEntity>, Float?> {
        val list: List<PillTakenEntity> = getPillTakenList(pillEntity,offset,limit)
        val firstTake: Float? = TimeConverter.convertISOToHours(list.firstOrNull()?.getTime())
        return Pair(list, firstTake)
    }

    override fun getPillTakenList(pillEntity: PillEntity, offset: Long, limit: Long): List<PillTakenEntity> {
        val list: List<PillTakenEntity> = getPillTakenQuery(pillEntity).find(offset,limit)
        return list.sortedBy { pillTakenEntity: PillTakenEntity -> pillTakenEntity.date } //ascending
    }

    override fun deleteAll() {
        return mPillTakenEntityBox.removeAll()
    }

    override fun getAllByPill(pillEntity: PillEntity): List<PillTakenEntity> {
        return getPillTakenQuery(pillEntity).find()
    }

    override fun getAll(): List<PillTakenEntity> {
        return mPillTakenEntityBox.query().build().find()
    }
}