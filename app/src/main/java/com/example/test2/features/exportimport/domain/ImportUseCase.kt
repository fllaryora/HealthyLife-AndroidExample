package com.example.test2.features.exportimport.domain

import com.example.test2.features.dailyactivity.data.local.ActivityDAO
import com.example.test2.features.numbertwo.data.local.NumberTwoDAO
import com.example.test2.features.pill.data.local.PillDAO
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAO
import com.example.test2.features.recordpill.data.local.PillTakenDAO
import com.example.test2.features.water.data.local.WaterDAO
import com.example.test2.features.weight.data.local.WeightDAO

interface ImportUseCase {
    fun initialize(
        weightDAO: WeightDAO,
        waterDAO: WaterDAO,
        numberTwoDAO: NumberTwoDAO,
        pillDAO: PillDAO,
        activityDAO: ActivityDAO,
        pillTakenDAO: PillTakenDAO,
        activityTakenDAO: ActivityTakenDAO
    )

    fun invokeImport(databaseString: String)
}