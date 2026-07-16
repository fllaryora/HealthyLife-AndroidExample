package com.example.test2.data.notification

import com.example.test2.data.entities.enums.TypeofRecorder
import java.io.Serializable

data class Notification(val notificationTitle: String,
                        val notificationTicker:String,
                        val type: TypeofRecorder,
                        val notificationCode:Int,
                        val activityId:Long,
                        val dayOfYear:Int,
                        val hour:Int,
                        val minute:Int) : Serializable