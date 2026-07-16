package com.example.test2.data.dao.implementations

import com.example.test2.data.dao.behaviors.TodoLineableDAO
import com.example.test2.data.entities.implementations.NumberTwo
import com.example.test2.data.entities.implementations.NumberTwo_
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

object NumberTwoDAO : TodoLineableDAO<NumberTwo> {

    private lateinit var mNumberTwoBox: Box<NumberTwo>
    private lateinit var mNumberTwoQuery: Query<NumberTwo>

    fun initialize(box: Box<NumberTwo>) {
        mNumberTwoBox = box
        mNumberTwoQuery = mNumberTwoBox.query()
        .order(
            NumberTwo_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE  ).build()

    }

    fun insert(numberTwo: NumberTwo) : Long {
        //return the new key
        try {
            return mNumberTwoBox.put(numberTwo)
        } catch (e: UniqueViolationException) {
            // A User with that name already exists.
            throw Exception(e.message)
        }
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(todoLineable: NumberTwo) : Boolean {
        return mNumberTwoBox.remove(todoLineable)
    }

    fun getNumberTwoList( offset: Long, limit: Long): List<NumberTwo> {
        return mNumberTwoQuery.find(offset,limit)
            .sortedBy { numberTwo: NumberTwo -> numberTwo.date } //ascending
    }

    fun deleteAll(){
        return mNumberTwoBox.removeAll()
    }

    fun getAll() : List<NumberTwo> {
        return mNumberTwoBox.query().build().find()
    }
}