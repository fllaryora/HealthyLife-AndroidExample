package com.example.test2.features.water.data.local

import com.example.test2.data.converter.TimeConverter
import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder
import io.objectbox.query.QueryCondition
import java.time.OffsetDateTime

object WaterDAOImpl : WaterDAO {

    private lateinit var mWaterBox: Box<WaterEntity>
    private lateinit var mWaterQuery: Query<WaterEntity>

    override fun getBox(): Box<WaterEntity> {
        return mWaterBox
    }


    override fun initialize(box: Box<WaterEntity>) {
        mWaterBox = box
        mWaterQuery = mWaterBox.query().order(
            WaterEntity_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE ).build()
    }

    override fun insert(water: WaterEntity) : Long {
        //return the new key
        return mWaterBox.put(water)
    }

    override fun delete(timelineable: WaterEntity): Boolean {
        return mWaterBox.remove(timelineable)
    }

    /**
     * the first point is today at midnight
     *
     * */
    override fun getIntakesByDay( day : OffsetDateTime): List<WaterEntity> {
        val stringDate = TimeConverter.fromOffsetDate(day)!!
        val conditions: QueryCondition<WaterEntity> = WaterEntity_.date.startsWith(stringDate)

        return  mWaterBox.query(
            conditions
        )

            .order(WaterEntity_.date, QueryBuilder.CASE_SENSITIVE ).build().find()
    }

    override fun getWaters(offset: Long, limit: Long): List<WaterEntity> {
        val list: List<WaterEntity> = mWaterQuery.find(offset, limit)
        return list.sortedBy { water: WaterEntity -> water.date } //ascending
    }

    override fun deleteAll() {
        return mWaterBox.removeAll()
    }

    override fun getAll() : List<WaterEntity>{
        return mWaterBox.query().build().find()
    }

}