package com.example.test2

import android.app.Application
import com.example.test2.data.dao.implementations.ActivityDAO
import com.example.test2.data.dao.implementations.ActivityTakenDAO
import com.example.test2.data.dao.implementations.NumberTwoDAO
import com.example.test2.data.dao.implementations.PillDAO
import com.example.test2.data.dao.implementations.PillTakenDAO
import com.example.test2.data.dao.implementations.WaterDAO
import com.example.test2.data.dao.implementations.WeightDAO
import com.example.test2.data.entities.implementations.ActivityTaken
import com.example.test2.data.entities.implementations.DailyActivity
import com.example.test2.data.entities.implementations.NumberTwo
import com.example.test2.data.entities.implementations.Pill
import com.example.test2.data.entities.implementations.PillTaken
import com.example.test2.data.entities.implementations.Water
import com.example.test2.data.entities.implementations.Weight
import com.example.test2.framework.data.database.ObjectBox
import io.objectbox.Box


class MyDBApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)

        val mWeightBox: Box<Weight> = ObjectBox.mBoxStore.boxFor(Weight::class.java)
        WeightDAO.initialize(mWeightBox)

        val mWaterBox: Box<Water> = ObjectBox.mBoxStore.boxFor(Water::class.java)
        WaterDAO.initialize(mWaterBox)

        val mPillBox: Box<Pill> = ObjectBox.mBoxStore.boxFor(Pill::class.java)
        PillDAO.initialize(mPillBox)

        val mPillTakenBox: Box<PillTaken> = ObjectBox.mBoxStore.boxFor(PillTaken::class.java)
        PillTakenDAO.initialize(mPillTakenBox)

        val mNumberTwoBox: Box<NumberTwo> = ObjectBox.mBoxStore.boxFor(NumberTwo::class.java)
        NumberTwoDAO.initialize(mNumberTwoBox)

        val mActivityBox : Box<DailyActivity> = ObjectBox.mBoxStore.boxFor(DailyActivity::class.java)
        ActivityDAO.initialize(mActivityBox)

        val mActivityTakenBox: Box<ActivityTaken>  = ObjectBox.mBoxStore.boxFor(ActivityTaken::class.java)
        ActivityTakenDAO.initialize(mActivityTakenBox)
    }

    /*For testing use
    BoxStore inMemoryStore = MyObjectBox.builder()
        .androidContext(context)
        .inMemory("test-db")
        .build();
    * */
}