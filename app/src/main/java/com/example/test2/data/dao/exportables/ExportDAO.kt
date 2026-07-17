package com.example.test2.data.dao.exportables

import com.example.test2.data.dao.implementations.ActivityDAO
import com.example.test2.data.dao.implementations.ActivityTakenDAO
import com.example.test2.data.dao.implementations.PillDAO
import com.example.test2.data.dao.implementations.WaterDAO
import com.example.test2.features.weight.data.local.WeightDAOImpl
import com.example.test2.data.entities.exportables.ExportEntity
import com.example.test2.data.entities.implementations.ActivityTaken
import com.example.test2.data.entities.implementations.DailyActivity
import com.example.test2.data.entities.implementations.NumberTwo
import com.example.test2.data.entities.implementations.Pill
import com.example.test2.data.entities.implementations.PillTaken
import com.example.test2.data.entities.implementations.Water
import com.example.test2.features.weight.data.local.WeightEntity
import android.util.Log
import com.example.test2.data.dao.implementations.NumberTwoDAO
import com.example.test2.data.dao.implementations.PillTakenDAO
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ExportDAO {

    fun getExport(): String {
        val weightEntities: List<WeightEntity> = WeightDAOImpl.getAll()
        val waters: List<Water> = WaterDAO.getAll()
        val numberTwos: List<NumberTwo> = NumberTwoDAO.getAll()
        val pills: List<Pill> = PillDAO.getPills()
        val dailyActivities : List<DailyActivity> = ActivityDAO.getActivities()
        val pillsTaken : List<PillTaken> = PillTakenDAO.getAll()
        val activitiesTaken: List<ActivityTaken> = ActivityTakenDAO.getAll()
        for(pillTaken in pillsTaken){
            pillTaken.exportPillId = pillTaken.pill.targetId
        }
        for(activityTaken in activitiesTaken){
            activityTaken.exportActivityId = activityTaken.activity.targetId
        }
        val export :ExportEntity = ExportEntity(
            dailyActivities = dailyActivities,
            activitiesTaken = activitiesTaken,
            numberTwos = numberTwos,
            pills = pills,
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
        val waters: List<Water> = importEntity.waters
        for (water in waters) {
            water.id = 0L
            try {
                WaterDAO.insert(water)
            } catch (e: Exception) {
                Log.e("ExportDAO water", e.message.toString())
            }
        }
    }

    private fun importBathroomVisits(importEntity: ExportEntity) {
        val numberTwos: List<NumberTwo> = importEntity.numberTwos
        for (numberTwo in numberTwos) {
            numberTwo.id = 0L
            try {
                NumberTwoDAO.insert(numberTwo)
            } catch (e: Exception) {
                // A User with that name already exists.
                Log.e("ExportDAO WC", e.message.toString())
            }
        }
    }

    private fun importPills(importEntity: ExportEntity) {
        val pills: List<Pill> = importEntity.pills

        for (pill in pills) {
            val oldId = pill.id
            var newId = 0L
            pill.id = 0L
            try {
                newId = PillDAO.insert(pill)
            } catch (e: Exception) {
                Log.e("ExportDAO PILL", e.message.toString())
            }
            importEntity.pillsTaken.filter { it.exportPillId == oldId }.forEach {
                it.pill.targetId = newId
                try {
                    PillTakenDAO.insert(it)
                } catch (e: Exception) {
                    // A User with that name already exists.
                    Log.e("ExportDAO PILL TAKEN", e.message.toString())
                }
            }
        }
    }

    private fun importActivities(importEntity: ExportEntity) {
        val activities: List<DailyActivity> = importEntity.dailyActivities

        for (currentActivity in activities) {
            val oldId = currentActivity.id
            var newId = 0L
            currentActivity.id = 0L
            try {
                newId = ActivityDAO.insert(currentActivity)
            } catch (e: Exception) {
                Log.e("ExportDAO ACTIVITY", e.message.toString())
            }
            importEntity.activitiesTaken.filter { it.exportActivityId == oldId }.forEach {
                it.activity.targetId = newId
                try {
                    ActivityTakenDAO.insert(it)
                } catch (e: Exception) {
                    // A User with that name already exists.
                    Log.e("ExportDAO ACTIVITY TAKEN", e.message.toString())
                }
            }
        }
    }
}