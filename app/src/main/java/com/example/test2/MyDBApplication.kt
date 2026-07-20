package com.example.test2

import android.app.Application
import com.example.test2.framework.data.database.di.objectBoxInitialization



class MyDBApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        objectBoxInitialization(this)


    }

    /*For testing use
    BoxStore inMemoryStore = MyObjectBox.builder()
        .androidContext(context)
        .inMemory("test-db")
        .build();
    * */
}