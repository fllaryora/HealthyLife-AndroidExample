package com.example.test2

import android.app.Application
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAOImpl
import com.example.test2.features.numbertwo.data.local.NumberTwoDAOImpl
import com.example.test2.features.pill.data.local.PillDAOImpl
import com.example.test2.features.recordpill.data.local.PillTakenDAOImpl
import com.example.test2.features.weight.data.local.WeightDAOImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.water.data.local.WaterDAOImpl
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.water.data.repository.WaterRepositoryImpl
import com.example.test2.features.weight.data.local.WeightEntity
import com.example.test2.features.weight.data.repository.WeightRepositoryImpl
import com.example.test2.framework.data.database.ObjectBox
import io.objectbox.Box
import kotlinx.coroutines.Dispatchers


class MyDBApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)

        val mWeightEntityBox: Box<WeightEntity> = ObjectBox.mBoxStore.boxFor(WeightEntity::class.java)
        WeightDAOImpl.initialize(mWeightEntityBox)
        WeightRepositoryImpl.initialize(WeightDAOImpl, Dispatchers.IO)

        val mWaterBox: Box<WaterEntity> = ObjectBox.mBoxStore.boxFor(WaterEntity::class.java)
        WaterDAOImpl.initialize(mWaterBox)
        WaterRepositoryImpl.initialize(WaterDAOImpl, Dispatchers.IO)

        val mPillEntityBox: Box<PillEntity> = ObjectBox.mBoxStore.boxFor(PillEntity::class.java)
        PillDAOImpl.initialize(mPillEntityBox)

        val mPillTakenEntityBox: Box<PillTakenEntity> = ObjectBox.mBoxStore.boxFor(PillTakenEntity::class.java)
        PillTakenDAOImpl.initialize(mPillTakenEntityBox)

        val mNumberTwoEntityBox: Box<NumberTwoEntity> = ObjectBox.mBoxStore.boxFor(NumberTwoEntity::class.java)
        NumberTwoDAOImpl.initialize(mNumberTwoEntityBox)

        val mActivityBox : Box<DailyActivityEntity> = ObjectBox.mBoxStore.boxFor(DailyActivityEntity::class.java)
        ActivityDAOImpl.initialize(mActivityBox)

        val mActivityTakenEntityBox: Box<ActivityTakenEntity>  = ObjectBox.mBoxStore.boxFor(ActivityTakenEntity::class.java)
        ActivityTakenDAOImpl.initialize(mActivityTakenEntityBox)

    }

    /*For testing use
    BoxStore inMemoryStore = MyObjectBox.builder()
        .androidContext(context)
        .inMemory("test-db")
        .build();
    * */
}