package com.example.test2.features.numbertwo.data.local

import com.example.test2.data.dao.behaviors.TodoLineableDAO
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

object NumberTwoDAOImpl : NumberTwoDAO {

    private lateinit var mNumberTwoEntityBox: Box<NumberTwoEntity>
    private lateinit var mNumberTwoEntityQuery: Query<NumberTwoEntity>

    override fun initialize(box: Box<NumberTwoEntity>) {
        mNumberTwoEntityBox = box
        mNumberTwoEntityQuery = mNumberTwoEntityBox.query()
        .order(
            NumberTwoEntity_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE  ).build()

    }

    override fun insert(numberTwoEntity: NumberTwoEntity) : Long {
        check(numberTwoEntity.id == 0L) {
            """
        ID is higher or equal to internal ID sequence: 1 (vs. 1). Use ID 0 (zero) to insert new objects.
        
        Should I export the ObjectBox IDs, or should I treat them as internal persistence details?
        No. The ID is infrastructure data.
        date + weight are the business data.
        
        """.trimIndent()
        }
        //return the new key
        try {
            return mNumberTwoEntityBox.put(numberTwoEntity)
        } catch (e: UniqueViolationException) {
            // A User with that name already exists.
            throw Exception(e.message)
        }
    }

    override fun getBox(): Box<NumberTwoEntity> {
        return mNumberTwoEntityBox
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: NumberTwoEntity) : Boolean {
        return mNumberTwoEntityBox.remove(todoLineable)
    }

    override fun getNumberTwoList( offset: Long, limit: Long): List<NumberTwoEntity> {
        return mNumberTwoEntityQuery.find(offset,limit)
            .sortedBy { numberTwoEntity: NumberTwoEntity -> numberTwoEntity.date } //ascending
    }

    override fun deleteAll(){
        return mNumberTwoEntityBox.removeAll()
    }

    override fun getAll() : List<NumberTwoEntity> {
        return mNumberTwoEntityBox.query().build().find()
    }
}