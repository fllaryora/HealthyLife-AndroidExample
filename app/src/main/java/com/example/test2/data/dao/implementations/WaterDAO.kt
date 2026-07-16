package com.example.test2.data.dao.implementations

import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.dao.behaviors.TimelineableDAO
import com.example.test2.data.entities.implementations.Water
import com.example.test2.data.entities.implementations.Water_
import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder
import io.objectbox.query.QueryCondition
import java.time.OffsetDateTime

object WaterDAO : TimelineableDAO<Water> {

    private lateinit var mWaterBox: Box<Water>
    private lateinit var mWaterQuery: Query<Water>

    fun initialize(box: Box<Water>) {
        mWaterBox = box
        mWaterQuery = mWaterBox.query().order(
            Water_.date, QueryBuilder.DESCENDING
                    or QueryBuilder.CASE_SENSITIVE ).build()
    }

    fun insert(water: Water) : Long {
        //return the new key
        return mWaterBox.put(water)
    }

    override fun delete(timelineable: Water): Boolean {
        return mWaterBox.remove(timelineable)
    }

    /**
     * the first point is today at midnight
     *
     * */
    fun getIntakesByDay( day : OffsetDateTime): List<Water> {
        val stringDate = TimeConverter.fromOffsetDate(day)!!
        val conditions: QueryCondition<Water> = Water_.date.startsWith(stringDate)

        return  mWaterBox.query(
            conditions
        )

            .order(Water_.date, QueryBuilder.CASE_SENSITIVE ).build().find()
    }

    fun getWaters(offset: Long, limit: Long): List<Water> {
        val list: List<Water> = mWaterQuery.find(offset, limit)
        return list.sortedBy { water: Water -> water.date } //ascending
    }

    fun deleteAll() {
        return mWaterBox.removeAll()
    }

    fun getAll() : List<Water>{
        return mWaterBox.query().build().find()
    }

}