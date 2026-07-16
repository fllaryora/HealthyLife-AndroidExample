package com.example.test2.data

import com.example.test2.data.dao.implementations.WaterDAO
import com.example.test2.data.entities.implementations.MyObjectBox
import com.example.test2.data.entities.implementations.Water
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.random.Random


open class WaterTest {

    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!

    private fun coin(from: Int, until: Int): Int {
        return Random(System.nanoTime()).nextInt(from = from, until = until)
    }

    private fun rand(): Float {
        return Random(System.nanoTime()).nextDouble(70.0, 180.0).toFloat()
    }



    @Before
    fun setUp() {
        // Delete any files in the test directory before each test to start with a clean database.
        BoxStore.deleteAllFiles(TEST_DIRECTORY)

        _store = MyObjectBox.builder()
            // Use a custom directory to store the database files in.
            .directory(TEST_DIRECTORY)
            // Optional: add debug flags for more detailed ObjectBox log output.
            .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
            .build()

        val mWaterBox: Box<Water> = store.boxFor(Water::class.java)
        WaterDAO.initialize(mWaterBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        // Get a box and use ObjectBox as usual
        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val list: List<Water> = WaterDAO.getWaters(0L, 20L)
        val allList : List<Water> = WaterDAO.getAll()
        val dayList : List<Water> = WaterDAO.getIntakesByDay(fixedTime)
        Assert.assertEquals(0, list.size)
        Assert.assertEquals(0, allList.size)
        Assert.assertEquals(0, dayList.size)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for( i  in 1..36) {
            WaterDAO.insert(Water(0L, someDayAtTheMorning, rand()))
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay).withHour(7).withMinute(0)
        }

        val list: List<Water> = WaterDAO.getWaters(0L, 20L)
        val allList : List<Water> = WaterDAO.getAll()
        Assert.assertEquals(20, list.size)
        Assert.assertEquals(36, allList.size)
        someDayAtTheMorning = sinceAYearAgoAtTheMorning
        for( i  in 1..36) {
            val dayList : List<Water>  = WaterDAO.getIntakesByDay(someDayAtTheMorning)
            Assert.assertEquals(1, dayList.size)
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay).withHour(7).withMinute(0)
        }


    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        WaterDAO.deleteAll()
    }

    @Test
    fun sparse() {
        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for( i  in 1..36) {
            WaterDAO.insert(Water(0L, someDayAtTheMorning, rand()))
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay).withHour(7).withMinute(0)
        }

        var allList = WaterDAO.getAll()
        val touch : Int  = coin(0, allList.size-1)
        allList.forEachIndexed { index, water : Water ->
            if( index != touch){
                WaterDAO.delete(water)
            }
        }

        allList = WaterDAO.getAll()

        Assert.assertEquals(1, allList.size)

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}