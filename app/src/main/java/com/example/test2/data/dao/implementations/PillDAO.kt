package com.example.test2.data.dao.implementations

import com.example.test2.data.entities.implementations.Pill
import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query

object PillDAO {

    private lateinit var mPillBox: Box<Pill>
    private lateinit var mPillQuery: Query<Pill>

    fun initialize(box: Box<Pill>) {
        mPillBox = box
        mPillQuery = mPillBox.query().build()
    }


    fun insert(pill: Pill) : Long {
        //return the new key
        try {
            return PillDAO.mPillBox.put(pill)
        } catch (e: UniqueViolationException) {
            // A User with that name already exists.
            throw Exception(e.message)
        }
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    fun delete(pill: Pill) : Boolean {
        return PillDAO.mPillBox.remove(pill)
    }

    fun getPills(): List<Pill> {
        return PillDAO.mPillQuery.find()
    }

    fun deleteAll(){
        return PillDAO.mPillBox.removeAll()
    }
}