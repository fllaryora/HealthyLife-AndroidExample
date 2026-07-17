package com.example.test2.features.weight.data.local

import com.example.test2.data.converter.TimeConverter
import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

object WeightDAOImpl : WeightDAO {

    lateinit var mWeightEntityBox: Box<WeightEntity>
    private lateinit var mWeightEntityQuery: Query<WeightEntity>

    override fun getBox(): Box<WeightEntity> {
        return mWeightEntityBox
    }

    override fun getQuery(): Query<WeightEntity> {
        return mWeightEntityQuery
    }

    override fun initialize(box: Box<WeightEntity>) {
        mWeightEntityBox = box
        mWeightEntityQuery = mWeightEntityBox.query().order(
            WeightEntity_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE ).build()


    }

    override fun insert(weightEntity: WeightEntity) : Long {
        //return the new key
        return mWeightEntityBox.put(weightEntity)
    }

    /**
     * Removes (deletes) the given Object.
     * @return true if an entity was actually removed (false if no entity exists with the given ID)
     */
    override fun delete(timelineable: WeightEntity) : Boolean {
        return mWeightEntityBox.remove(timelineable)
    }

    override fun getWeightsAndFirstDay(offset: Long, limit: Long): Pair<List<WeightEntity>, Float?> {
        val list: List<WeightEntity> = getWeights(offset, limit)
        val firstMeasure: Float? = TimeConverter.convertISOToHours(list.firstOrNull()?.getTime())
        return Pair(list, firstMeasure)
    }

    override fun getWeights(offset: Long, limit: Long): List<WeightEntity> {
        val list: List<WeightEntity> = mWeightEntityQuery.find(offset, limit)
        return list.sortedBy { weightEntity: WeightEntity -> weightEntity.date } //ascending
    }

    override fun deleteAll(){
        return mWeightEntityBox.removeAll()
    }

    override fun getAll() : List<WeightEntity>{
        return mWeightEntityBox.query().build().find()
    }

}