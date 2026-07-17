package com.example.test2.numbertwo.data

import com.example.test2.features.MyObjectBox
import com.example.test2.features.numbertwo.data.local.NumberTwoDAOImpl
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
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

open class NumberTwoEntityTest {

    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!

    private fun coin(from: Int, until: Int): Int {
        return Random(System.nanoTime()).nextInt(from = from, until = until)
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

        val mNumberTwoEntityBox: Box<NumberTwoEntity> = store.boxFor(NumberTwoEntity::class.java)
        NumberTwoDAOImpl.initialize(mNumberTwoEntityBox)
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
        val list: List<NumberTwoEntity> = NumberTwoDAOImpl.getNumberTwoList(0L, 20L)
        val allList : List<NumberTwoEntity> = NumberTwoDAOImpl.getAll()

        Assert.assertEquals(0, list.size)
        Assert.assertEquals(0, allList.size)

    }

    @Test
    fun insetWeightsAndGetTheData() {
        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val days = 50
        var lastInsert = 0L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for( i  in 1..days) {
            lastInsert = NumberTwoDAOImpl.insert(NumberTwoEntity(0L, someDayAtTheMorning))
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }

        val list: List<NumberTwoEntity> = NumberTwoDAOImpl.getNumberTwoList(0L, 20L)
        val allList : List<NumberTwoEntity> = NumberTwoDAOImpl.getAll()
        Assert.assertEquals(20, list.size)
        Assert.assertEquals(days, allList.size)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        NumberTwoDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {
        val fixedTime: OffsetDateTime = OffsetDateTime.of(
                2025, 10, 25, 14, 30, 0, 0,
        ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val days = 50
        var lastInsert = 0L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for( i  in 1..days) {
            lastInsert = NumberTwoDAOImpl.insert(NumberTwoEntity(0L, someDayAtTheMorning))
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }

        var allList = NumberTwoDAOImpl.getAll()
        val touch : Int  = coin(0, allList.size-1)
        allList.forEachIndexed { index, water : NumberTwoEntity ->
            if( index != touch){
                NumberTwoDAOImpl.delete(water)
            }
        }

        val list: List<NumberTwoEntity> = NumberTwoDAOImpl.getNumberTwoList(0L, 20L)
        allList = NumberTwoDAOImpl.getAll()
        Assert.assertEquals(1, list.size)
        Assert.assertEquals(1, allList.size)

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}