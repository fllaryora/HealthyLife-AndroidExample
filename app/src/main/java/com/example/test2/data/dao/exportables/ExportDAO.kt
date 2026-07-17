package com.example.test2.data.dao.exportables

import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAOImpl
import com.example.test2.features.pill.data.local.PillDAOImpl
import com.example.test2.features.weight.data.local.WeightDAOImpl
import com.example.test2.data.entities.exportables.ExportEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.weight.data.local.WeightEntity
import android.util.Log
import com.example.test2.features.numbertwo.data.local.NumberTwoDAOImpl
import com.example.test2.features.recordpill.data.local.PillTakenDAOImpl
import com.example.test2.features.water.data.local.WaterDAOImpl
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ExportDAO {

    fun getExport(): String {
        val weightEntities: List<WeightEntity> = WeightDAOImpl.getAll()
        val waters: List<WaterEntity> = WaterDAOImpl.getAll()
        val numberTwoEntities: List<NumberTwoEntity> = NumberTwoDAOImpl.getAll()
        val pillEntities: List<PillEntity> = PillDAOImpl.getPills()
        val dailyActivities : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()
        val pillsTaken : List<PillTakenEntity> = PillTakenDAOImpl.getAll()
        val activitiesTaken: List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()
        for(pillTaken in pillsTaken){
            pillTaken.exportPillId = pillTaken.pillEntity.targetId
        }
        for(activityTaken in activitiesTaken){
            activityTaken.exportActivityId = activityTaken.activity.targetId
        }
        val export :ExportEntity = ExportEntity(
            dailyActivities = dailyActivities,
            activitiesTaken = activitiesTaken,
            numberTwoEntities = numberTwoEntities,
            pillEntities = pillEntities,
            pillsTaken = pillsTaken,
            waters = waters,
            weightEntities = weightEntities
        )
        return Json.encodeToString(export)
    }

    fun setImport(databaseString: String) {
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
    }

    private fun importWeights(importEntity: ExportEntity) {
        val weightEntities: List<WeightEntity> = importEntity.weightEntities
        for (weight in weightEntities) {
            weight.id = 0L
            try {
                WeightDAOImpl.insert(weight)
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
                WaterDAOImpl.insert(water)
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
                NumberTwoDAOImpl.insert(numberTwo)
            } catch (e: Exception) {
                // A User with that name already exists.
                Log.e("ExportDAO WC", e.message.toString())
            }
        }
    }

    private fun importPills(importEntity: ExportEntity) {
        val pillEntities: List<PillEntity> = importEntity.pillEntities

        for (pill in pillEntities) {
            val oldId = pill.id
            var newId = 0L
            pill.id = 0L
            try {
                newId = PillDAOImpl.insert(pill)
            } catch (e: Exception) {
                Log.e("ExportDAO PILL", e.message.toString())
            }
            importEntity.pillsTaken.filter { it.exportPillId == oldId }.forEach {
                it.pillEntity.targetId = newId
                try {
                    PillTakenDAOImpl.insert(it)
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
                newId = ActivityDAOImpl.insert(currentActivity)
            } catch (e: Exception) {
                Log.e("ExportDAO ACTIVITY", e.message.toString())
            }
            importEntity.activitiesTaken.filter { it.exportActivityId == oldId }.forEach {
                it.activity.targetId = newId
                try {
                    ActivityTakenDAOImpl.insert(it)
                } catch (e: Exception) {
                    // A User with that name already exists.
                    Log.e("ExportDAO ACTIVITY TAKEN", e.message.toString())
                }
            }
        }
    }
}