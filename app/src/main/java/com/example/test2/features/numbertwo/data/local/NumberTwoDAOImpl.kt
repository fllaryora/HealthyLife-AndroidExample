package com.example.test2.features.numbertwo.data.local

import com.example.test2.data.dao.behaviors.TodoLineableDAO
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

object NumberTwoDAOImpl : TodoLineableDAO<NumberTwoEntity> {

    private lateinit var mNumberTwoEntityBox: Box<NumberTwoEntity>
    private lateinit var mNumberTwoEntityQuery: Query<NumberTwoEntity>

    fun initialize(box: Box<NumberTwoEntity>) {
        mNumberTwoEntityBox = box
        mNumberTwoEntityQuery = mNumberTwoEntityBox.query()
        .order(
            NumberTwoEntity_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE  ).build()

    }

    fun insert(numberTwoEntity: NumberTwoEntity) : Long {
        //return the new key
        try {
            return mNumberTwoEntityBox.put(numberTwoEntity)
        } catch (e: UniqueViolationException) {
            // A User with that name already exists.
            throw Exception(e.message)
        }
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: NumberTwoEntity) : Boolean {
        return mNumberTwoEntityBox.remove(todoLineable)
    }

    fun getNumberTwoList( offset: Long, limit: Long): List<NumberTwoEntity> {
        return mNumberTwoEntityQuery.find(offset,limit)
            .sortedBy { numberTwoEntity: NumberTwoEntity -> numberTwoEntity.date } //ascending
    }

    fun deleteAll(){
        return mNumberTwoEntityBox.removeAll()
    }

    fun getAll() : List<NumberTwoEntity> {
        return mNumberTwoEntityBox.query().build().find()
    }
}