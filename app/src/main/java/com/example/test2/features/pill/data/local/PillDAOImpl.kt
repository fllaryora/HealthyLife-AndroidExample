package com.example.test2.features.pill.data.local

import io.objectbox.Box
import io.objectbox.exception.UniqueViolationException
import io.objectbox.query.Query

object PillDAOImpl: PillDAO {

    private lateinit var mPillEntityBox: Box<PillEntity>
    private lateinit var mPillEntityQuery: Query<PillEntity>

    override fun getBox() : Box<PillEntity> {
        return mPillEntityBox
    }

    override fun initialize(box: Box<PillEntity>) {
        mPillEntityBox = box
        mPillEntityQuery = mPillEntityBox.query().build()
    }


    override fun insert(pillEntity: PillEntity) : Long {
        //return the new key
        try {
            return mPillEntityBox.put(pillEntity)
        } catch (e: UniqueViolationException) {
            // A User with that name already exists.
            throw Exception(e.message)
        }
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(pillEntity: PillEntity) : Boolean {
        return mPillEntityBox.remove(pillEntity)
    }

    override fun getPills(): List<PillEntity> {
        return mPillEntityQuery.find()
    }

    override fun deleteAll(){
        return mPillEntityBox.removeAll()
    }
}