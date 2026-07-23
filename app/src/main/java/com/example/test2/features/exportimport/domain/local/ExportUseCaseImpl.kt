package com.example.test2.features.exportimport.domain.local

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
import com.example.test2.features.weight.data.local.WeightEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

val jsonPropertiesForExport = Json {
    prettyPrint = true
    encodeDefaults = true
}
object ExportUseCaseImpl : ExportUseCase {
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
    override fun invokeExport(): String {
        if(isInit) {
            val weightEntities: List<WeightEntity> = mWightDAO.getAll()
            val waters: List<WaterEntity> = mWaterDAO.getAll()
            val numberTwoEntities: List<NumberTwoEntity> = mNumberTwoDAO.getAll()
            val pillEntities: List<PillEntity> = mPillDAO.getPills()
            val dailyActivities : List<DailyActivityEntity> = mActivityDAO.getActivities()
            val pillsTaken : List<PillTakenEntity> = mPillTakenDAO.getAll()
            val activitiesTaken: List<ActivityTakenEntity> = maActivityTakenDAO.getAll()

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
            return jsonPropertiesForExport.encodeToString(export)
        } else {
            throw Exception("ExportUseCaseImpl Not init invokeExport")
        }
    }
}