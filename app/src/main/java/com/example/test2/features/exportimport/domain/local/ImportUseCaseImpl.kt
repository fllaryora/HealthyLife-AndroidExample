package com.example.test2.features.exportimport.domain.local

//import android.util.Log

import com.example.test2.data.entities.behaviors.groupAndImportResolvingOwners
import com.example.test2.data.entities.behaviors.importAndGetComparableIDsMap
import com.example.test2.data.entities.behaviors.prepareForImport
import com.example.test2.features.dailyactivity.data.local.ActivityDAO
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.exportimport.data.local.ExportEntity
import com.example.test2.features.numbertwo.data.local.NumberTwoDAO
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.pill.data.local.PillDAO
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAO
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.recordpill.data.local.PillTakenDAO
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.water.data.local.WaterDAO
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.weight.data.local.WeightDAO
import com.example.test2.features.weight.data.local.WeightDAOImpl
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
                throw Exception("ImportUseCaseImpl setImport", e)
            }
        } else {
            throw Exception("ImportUseCaseImpl Not init invokeImport")
        }
    }

    private fun importWeights(importEntity: ExportEntity) {
        val importedWeights: List<WeightEntity> = importEntity.weightEntities

        importedWeights.prepareForImport().forEach { weightEntity: WeightEntity ->
            mWightDAO.insert(weightEntity)
        }
    }

    private fun importWaters(importEntity: ExportEntity) {
        val waters: List<WaterEntity> = importEntity.waters
        waters.prepareForImport().forEach { waterEntity : WaterEntity->
            mWaterDAO.insert(waterEntity)
        }
    }

    private fun importBathroomVisits(importEntity: ExportEntity) {
        val numberTwoEntities: List<NumberTwoEntity> = importEntity.numberTwoEntities
        numberTwoEntities.prepareForImport().forEach { numberTwoEntity: NumberTwoEntity ->
            mNumberTwoDAO.insert(numberTwoEntity)
        }
    }

    private fun importPills(importEntity: ExportEntity) {
        val pillEntities: List<PillEntity> = importEntity.pillEntities
        val pillsTakenEntities: List<PillTakenEntity> = importEntity.pillsTaken

        val importedPillsByOldId: Map<Long, PillEntity > =
            pillEntities.importAndGetComparableIDsMap( insert = { pillToInsert: PillEntity ->
                mPillDAO.insert(pillToInsert)
            } )

        pillsTakenEntities.groupAndImportResolvingOwners(
            importedOwnersByOldId = importedPillsByOldId,
            insert = { prepared: PillTakenEntity ->
                mPillTakenDAO.insert(prepared)
            })
    }

    private fun importActivities(importEntity: ExportEntity) {
        val activities: List<DailyActivityEntity> = importEntity.dailyActivities
        val activitiesTakenEntities: List<ActivityTakenEntity> = importEntity.activitiesTaken

        val importedActivitiesByOldId: Map<Long, DailyActivityEntity> =
            activities.importAndGetComparableIDsMap(insert = { activityToInsert: DailyActivityEntity ->
                mActivityDAO.insert(activityToInsert)
            })

        // Arrange
        activitiesTakenEntities.groupAndImportResolvingOwners(
            importedOwnersByOldId = importedActivitiesByOldId,
            insert = { prepared: ActivityTakenEntity ->
                maActivityTakenDAO.insert(prepared)
            })
    }

}