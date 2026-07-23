package com.example.test2

import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.weight.data.local.WeightEntity
import java.time.OffsetDateTime
import java.time.ZoneOffset

object TestDateFactory {

    fun dailySequence(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
        nano: Int,
        zoneOffset: ZoneOffset = ZoneOffset.ofHours(-3), // Example: UTC-3 for Argentina
        daysBack: Long = 35,
        targetHour: Int = 7,
        targetMinute: Int = 0
    ): Sequence<OffsetDateTime> {

        val start = OffsetDateTime.of(
            year,
            month,
            day,
            hour,
            minute,
            second,
            nano,
            zoneOffset
        )
            .minusDays(daysBack)
            .withHour(targetHour)
            .withMinute(targetMinute)
            .withSecond(0)
            .withNano(0)

        val aDay = 1L
        return generateSequence(start) { current ->
            current.plusDays(aDay)
                .withHour(targetHour)
                .withMinute(targetMinute)
        }
    }


    fun weightSequence(): Sequence<Float> {
        return generateSequence(69.0f) { current: Float ->
            var nextElement : Float = current + 1.0f
            if(nextElement > 180.0f ) {
                nextElement = 70.0f
            }
            return@generateSequence nextElement
        }
    }

    fun ratingSequence(): Sequence<Int> {
        return generateSequence(0) { current: Int ->
            var nextElement : Int = current + 1
            if(nextElement > 10 ) {
                nextElement = 1
            }
            return@generateSequence nextElement
        }
    }

    fun buildWeights(year: Int,
                     month: Int,
                     day: Int,
                     hour: Int,
                     minute: Int,
                     second: Int,
                     nano: Int,
                     amount: Int
                     ): List<WeightEntity> {
        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            year,
            month,
            day,
            hour,
            minute,
            second,
            nano,
        )
        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()

        return dates.take(amount)
            .map { someDayAtTheMorning: OffsetDateTime ->
                WeightEntity(0L, someDayAtTheMorning, iterator.next())
            }.toList()
    }

    fun buildWaters(year: Int,
                     month: Int,
                     day: Int,
                     hour: Int,
                     minute: Int,
                     second: Int,
                     nano: Int,
                     amount: Int
    ): List<WaterEntity> {
        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            year,
            month,
            day,
            hour,
            minute,
            second,
            nano,
        )
        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()

        return dates.take(amount)
            .map { someDayAtTheMorning: OffsetDateTime ->
                WaterEntity(0L, someDayAtTheMorning, iterator.next())
            }.toList()
    }

    fun buildNumberTwo(year: Int,
                    month: Int,
                    day: Int,
                    hour: Int,
                    minute: Int,
                    second: Int,
                    nano: Int,
                    amount: Int
    ): List<NumberTwoEntity> {
        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            year,
            month,
            day,
            hour,
            minute,
            second,
            nano,
        )
        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()

        return dates.take(amount)
            .map { someDayAtTheMorning: OffsetDateTime ->
                NumberTwoEntity(0L, someDayAtTheMorning, )
            }.toList()
    }

}