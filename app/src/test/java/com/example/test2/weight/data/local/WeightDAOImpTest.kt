package com.example.test2.weight.data.local

import com.example.test2.MyObjectBox
import com.example.test2.features.weight.data.local.WeightDAOImpl
import com.example.test2.features.weight.data.local.WeightEntity
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

open class WeightDAOImpTest {

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

        val mWeightEntityBox: Box<WeightEntity> = store.boxFor(WeightEntity::class.java)
        WeightDAOImpl.initialize(mWeightEntityBox)
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
        val list: List<WeightEntity> = WeightDAOImpl.getWeights(0L, 20L)
        val allList : List<WeightEntity> = WeightDAOImpl.getAll()
        val pair : Pair<List<WeightEntity>, Float?> = WeightDAOImpl.getWeightsAndFirstDay(0L, 20L)
        Assert.assertEquals(0, list.size)
        Assert.assertEquals(0, allList.size)
        Assert.assertEquals(0, pair.first.size)
        Assert.assertEquals(null, pair.second)
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
            WeightDAOImpl.insert(WeightEntity(0L, someDayAtTheMorning, rand()))
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay).withHour(7).withMinute(0)
        }

        val list: List<WeightEntity> = WeightDAOImpl.getWeights(0L, 20L)
        val allList : List<WeightEntity> = WeightDAOImpl.getAll()
        val pair : Pair<List<WeightEntity>, Float?> = WeightDAOImpl.getWeightsAndFirstDay(0L, 20L)
        Assert.assertEquals(20, list.size)
        Assert.assertEquals(36, allList.size)
        Assert.assertEquals(20, pair.first.size)
        Assert.assertEquals(488818.0f, pair.second)
    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        WeightDAOImpl.deleteAll()
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
            WeightDAOImpl.insert(WeightEntity(0L, someDayAtTheMorning, rand()))
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay).withHour(7).withMinute(0)
        }

        var allList = WeightDAOImpl.getAll()
        val touch : Int  = coin(0, allList.size-1)
        allList.forEachIndexed { index, weightEntity : WeightEntity ->
            if( index != touch){
                WeightDAOImpl.delete(weightEntity)
            }
        }

        allList = WeightDAOImpl.getAll()

        Assert.assertEquals(1, allList.size)

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}