package com.example.test2.data.entities.behaviors

import java.time.OffsetDateTime

interface Timelineable {
    fun getTime() : OffsetDateTime
    fun getLabel() : String
}