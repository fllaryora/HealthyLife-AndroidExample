package com.example.test2.data.dao.implementations

import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.dao.behaviors.TimelineableDAO
import com.example.test2.data.entities.implementations.Weight
import com.example.test2.data.entities.implementations.Weight_
import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

object WeightDAO : TimelineableDAO<Weight> {


    private lateinit var mWeightBox: Box<Weight>
    private lateinit var mWeightQuery: Query<Weight>

    fun initialize(box: Box<Weight>) {
        mWeightBox = box
        mWeightQuery = mWeightBox.query().order(
            Weight_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE ).build()

    }

    fun insert(weight: Weight) : Long {
        //return the new key
        return mWeightBox.put(weight)
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(timelineable: Weight) : Boolean {
        return mWeightBox.remove(timelineable)
    }

    fun getWeightsAndFirstDay(offset: Long, limit: Long): Pair<List<Weight>, Float?> {
        val list: List<Weight> = getWeights(offset, limit)
        val firstMeasure: Float? = TimeConverter.convertISOToHours(list.firstOrNull()?.getTime())
        return Pair(list, firstMeasure)
    }

    fun getWeights(offset: Long, limit: Long): List<Weight> {
        val list: List<Weight> = mWeightQuery.find(offset, limit)
        return list.sortedBy { weight: Weight -> weight.date } //ascending
    }

    fun deleteAll(){
        return mWeightBox.removeAll()
    }

    fun getAll() : List<Weight>{
        return mWeightBox.query().build().find()
    }
}