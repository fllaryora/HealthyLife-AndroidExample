package com.example.test2.features.exportimport.domain

import android.util.Log

import com.example.test2.features.dailyactivity.data.local.ActivityDAO
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.exportimport.data.local.ExportEntity
import com.example.test2.features.numbertwo.data.local.NumberTwoDAO
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.pill.data.local.PillDAO
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAO
import com.example.test2.features.recordpill.data.local.PillTakenDAO
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.water.data.local.WaterDAO
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.weight.data.local.WeightDAO
import com.example.test2.features.weight.data.local.WeightEntity
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object ImportUseCaseImpl : ImportUseCase {
    private var isInit: Boolean = false

    private lateinit var mWightDAO: WeightDAO
    private lateinit var mWaterDAO: WaterDAO
    private lateinit var mNumberTwoDAO: NumberTwoDAO
    private lateinit var mPillDAO: PillDAO
    private lateinit var mActivityDAO: ActivityDAO
    private lateinit var mPillTakenDAO: PillTakenDAO
    private lateinit var maActivityTakenDAO: ActivityTakenDAO
    override fun initialize(
        weightDAO: WeightDAO,
        waterDAO: WaterDAO,
        numberTwoDAO: NumberTwoDAO,
        pillDAO: PillDAO,
        activityDAO: ActivityDAO,
        pillTakenDAO: PillTakenDAO,
        activityTakenDAO: ActivityTakenDAO
    ) {
        mWightDAO = weightDAO
       mWaterDAO = waterDAO
         mNumberTwoDAO = numberTwoDAO
         mPillDAO = pillDAO
         mActivityDAO = activityDAO
       mPillTakenDAO = pillTakenDAO
         maActivityTakenDAO = activityTakenDAO
        isInit = true
    }

    override fun invokeImport(databaseString: String) {
        if (isInit) {
            try {
                val importEntity :ExportEntity = Json.decodeFromString<ExportEntity>(databaseString)
                importWeights(importEntity)
                importWaters(importEntity)
                importBathroomVisits(importEntity)
                importPills(importEntity)
                importActivities(importEntity)
            } catch( e: SerializationException) {
                Log.e("ExportDAO", "Error setImport")
            }
        } else {
            throw Exception("ImportUseCaseImpl Not init invokeImport")
        }
    }


    private fun importWeights(importEntity: ExportEntity) {
        val weightEntities: List<WeightEntity> = importEntity.weightEntities
        for (weight in weightEntities) {
            weight.id = 0L
            try {
                mWightDAO.insert(weight)
            } catch (e: Exception) {
                Log.e("ExportDAO weight", e.message.toString())
            }
        }
    }

    private fun importWaters(importEntity: ExportEntity) {
        val waters: List<WaterEntity> = importEntity.waters
        for (water in waters) {
            water.id = 0L
            try {
                mWaterDAO.insert(water)
            } catch (e: Exception) {
                Log.e("ExportDAO water", e.message.toString())
            }
        }
    }

    private fun importBathroomVisits(importEntity: ExportEntity) {
        val numberTwoEntities: List<NumberTwoEntity> = importEntity.numberTwoEntities
        for (numberTwo in numberTwoEntities) {
            numberTwo.id = 0L
            try {
                mNumberTwoDAO.insert(numberTwo)
            } catch (e: Exception) {
                // A User with that name already exists.
                Log.e("ExportDAO WC", e.message.toString())
            }
        }
    }

    private fun importPills(importEntity: ExportEntity) {
        val pillEntities: List<PillEntity> = importEntity.pillEntities

        val pillsTakenByPillId: Map<Long, List<PillTakenEntity>> = importEntity.pillsTaken
            .groupBy { pillTaken: PillTakenEntity ->  pillTaken.exportPillId }

        for (pill: PillEntity in pillEntities) {

            val oldId = pill.id
            var newId: Long
            pill.id = 0L
            try {
                newId = mPillDAO.insert(pill)
            } catch (e: Exception) {
                Log.e("ExportDAO PILL", e.message.toString())
                continue
            }
            pillsTakenByPillId[ oldId ]?.forEach { pillTaken: PillTakenEntity ->
                pillTaken.pillEntity.targetId = newId
                try {
                    mPillTakenDAO.insert(pillTaken)
                } catch (e: Exception) {
                    // A User with that name already exists.
                    Log.e("ExportDAO PILL TAKEN", e.message.toString())
                }
            }

        }
    }

    private fun importActivities(importEntity: ExportEntity) {
        val activities: List<DailyActivityEntity> = importEntity.dailyActivities

        for (currentActivity in activities) {
            val oldId = currentActivity.id
            var newId = 0L
            currentActivity.id = 0L
            try {
                newId = mActivityDAO.insert(currentActivity)
            } catch (e: Exception) {
                Log.e("ExportDAO ACTIVITY", e.message.toString())
            }
            importEntity.activitiesTaken.filter { it.exportActivityId == oldId }.forEach {
                it.activity.targetId = newId
                try {
                    maActivityTakenDAO.insert(it)
                } catch (e: Exception) {
                    // A User with that name already exists.
                    Log.e("ExportDAO ACTIVITY TAKEN", e.message.toString())
                }
            }
        }
    }
}