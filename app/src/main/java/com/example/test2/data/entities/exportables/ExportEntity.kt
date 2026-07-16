package com.example.test2.data.entities.exportables

import com.example.test2.data.entities.implementations.ActivityTaken
import com.example.test2.data.entities.implementations.DailyActivity
import com.example.test2.data.entities.implementations.NumberTwo
import com.example.test2.data.entities.implementations.Pill
import com.example.test2.data.entities.implementations.PillTaken
import com.example.test2.data.entities.implementations.Water
import com.example.test2.data.entities.implementations.Weight
import kotlinx.serialization.Serializable

@Serializable
data class ExportEntity(
    val dailyActivities: List<DailyActivity>,
    val activitiesTaken: List<ActivityTaken>,
    val numberTwos: List<NumberTwo>,
    val pills: List<Pill>,
    val pillsTaken: List<PillTaken>,
    val waters: List<Water>,
    val weights: List<Weight>
)