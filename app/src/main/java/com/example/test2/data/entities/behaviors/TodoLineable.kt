package com.example.test2.data.entities.behaviors

import java.time.OffsetDateTime

interface TodoLineable {
    fun getTime() : OffsetDateTime
    fun getRatingT() : Int
    fun getShowRating() : Boolean
    fun isTakenT() : Boolean
}