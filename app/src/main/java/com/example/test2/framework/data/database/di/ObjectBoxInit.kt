package com.example.test2.framework.data.database.di

import android.app.Application
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepositoryImpl
import com.example.test2.features.dailyactivity.domain.ActivityUseCaseImpl
import com.example.test2.features.dailyactivity.domain.repository.ActivityUseCaseRepositoryImpl
import com.example.test2.features.exportimport.domain.ExportUseCaseImpl
import com.example.test2.features.exportimport.domain.ImportUseCaseImpl
import com.example.test2.features.numbertwo.data.local.NumberTwoDAOImpl
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.numbertwo.data.repository.NumberTwoRepositoryImpl
import com.example.test2.features.pill.data.local.PillDAOImpl
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.pill.data.repository.PillRepositoryImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAOImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.recordactivity.data.repository.ActivityTakenRepositoryImpl
import com.example.test2.features.recordpill.data.local.PillTakenDAOImpl
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.recordpill.data.repository.PillTakenRepositoryImpl
import com.example.test2.features.water.data.local.WaterDAOImpl
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.water.data.repository.WaterRepositoryImpl
import com.example.test2.features.weight.data.local.WeightDAOImpl
import com.example.test2.features.weight.data.local.WeightEntity
import com.example.test2.features.weight.data.repository.WeightRepositoryImpl
import com.example.test2.framework.data.database.ObjectBox
import io.objectbox.Box
import kotlinx.coroutines.Dispatchers

fun objectBoxInitialization(app : Application) {
    ObjectBox.init(app)

    val mWeightEntityBox: Box<WeightEntity> = ObjectBox.mBoxStore.boxFor(WeightEntity::class.java)
    WeightDAOImpl.initialize(mWeightEntityBox)
    WeightRepositoryImpl.initialize(WeightDAOImpl, Dispatchers.IO)

    val mWaterBox: Box<WaterEntity> = ObjectBox.mBoxStore.boxFor(WaterEntity::class.java)
    WaterDAOImpl.initialize(mWaterBox)
    WaterRepositoryImpl.initialize(WaterDAOImpl, Dispatchers.IO)

    val mNumberTwoEntityBox: Box<NumberTwoEntity> = ObjectBox.mBoxStore.boxFor(NumberTwoEntity::class.java)
    NumberTwoDAOImpl.initialize(mNumberTwoEntityBox)
    NumberTwoRepositoryImpl.initialize(NumberTwoDAOImpl, Dispatchers.IO)

    val mPillEntityBox: Box<PillEntity> = ObjectBox.mBoxStore.boxFor(PillEntity::class.java)
    PillDAOImpl.initialize(mPillEntityBox)
    PillRepositoryImpl.initialize(PillDAOImpl, Dispatchers.IO)

    val mActivityBox : Box<DailyActivityEntity> = ObjectBox.mBoxStore.boxFor(DailyActivityEntity::class.java)
    ActivityDAOImpl.initialize(mActivityBox)
    ActivityUseCaseImpl.initialize(ActivityDAOImpl)
    ActivityRepositoryImpl.initialize(ActivityDAOImpl, Dispatchers.IO)
    ActivityUseCaseRepositoryImpl.initialize(ActivityRepositoryImpl, Dispatchers.IO)


    val mPillTakenEntityBox: Box<PillTakenEntity> = ObjectBox.mBoxStore.boxFor(PillTakenEntity::class.java)
    PillTakenDAOImpl.initialize(mPillTakenEntityBox)
    PillTakenRepositoryImpl.initialize(PillTakenDAOImpl,  Dispatchers.IO)

    val mActivityTakenEntityBox: Box<ActivityTakenEntity>  = ObjectBox.mBoxStore.boxFor(ActivityTakenEntity::class.java)
    ActivityTakenDAOImpl.initialize(mActivityTakenEntityBox)
    ActivityTakenRepositoryImpl.initialize(ActivityTakenDAOImpl,  Dispatchers.IO)

    ExportUseCaseImpl.initialize(
        weightDAO = WeightDAOImpl,
        waterDAO=  WaterDAOImpl,
        numberTwoDAO= NumberTwoDAOImpl,
        pillDAO = PillDAOImpl,
        activityDAO = ActivityDAOImpl,
        pillTakenDAO = PillTakenDAOImpl,
        activityTakenDAO = ActivityTakenDAOImpl)
    
    ImportUseCaseImpl.initialize(
        weightDAO = WeightDAOImpl,
        waterDAO=  WaterDAOImpl,
        numberTwoDAO= NumberTwoDAOImpl,
        pillDAO = PillDAOImpl,
        activityDAO = ActivityDAOImpl,
        pillTakenDAO = PillTakenDAOImpl,
        activityTakenDAO = ActivityTakenDAOImpl)

}