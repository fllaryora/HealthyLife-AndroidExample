package com.example.test2.features.exportimport.data.local

import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.weight.data.local.WeightEntity
import kotlinx.serialization.Serializable

@Serializable
data class ExportEntity(
    val dailyActivities: List<DailyActivityEntity>,
    val activitiesTaken: List<ActivityTakenEntity>,
    val numberTwoEntities: List<NumberTwoEntity>,
    val pillEntities: List<PillEntity>,
    val pillsTaken: List<PillTakenEntity>,
    val waters: List<WaterEntity>,
    val weightEntities: List<WeightEntity>
)