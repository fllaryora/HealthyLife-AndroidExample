package com.example.test2.schedulepill.data

import com.example.test2.features.MyObjectBox
import com.example.test2.data.converter.TimeConverter
import com.example.test2.features.pill.data.local.PillDAOImpl
import com.example.test2.features.recordpill.data.local.PillTakenDAOImpl
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordpill.data.local.PillTakenEntity
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

open class PillEntityTakenTest {

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

        val mPillEntityBox: Box<PillEntity> = store.boxFor(PillEntity::class.java)
        PillDAOImpl.initialize(mPillEntityBox)
        val mPillTakenEntityBox: Box<PillTakenEntity> = store.boxFor(PillTakenEntity::class.java)
        PillTakenDAOImpl.initialize(mPillTakenEntityBox)

    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)
        val pillEntityList : List<PillEntity> = PillDAOImpl.getPills()


        val allList : List<PillTakenEntity> = PillTakenDAOImpl.getAll()
        Assert.assertEquals(0, allList.size)
        val allTakenPill : List<PillTakenEntity> = PillTakenDAOImpl.getAllByPill(pillEntityList.first())
        Assert.assertEquals(0, allTakenPill.size)
        val allTakenPillLimited : List<PillTakenEntity> = PillTakenDAOImpl.getPillTakenList(pillEntityList.first(), 0L, 20L)
        Assert.assertEquals(0, allTakenPillLimited.size)
        val allTakenPillLimitedPair : Pair<List<PillTakenEntity>,Float?> = PillTakenDAOImpl.getPillTaken(pillEntityList.first(), 0L, 20L)
        Assert.assertEquals(0, allTakenPillLimitedPair.first.size)
        Assert.assertEquals(null, allTakenPillLimitedPair.second)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)
        val pillEntityList : List<PillEntity> = PillDAOImpl.getPills()

        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for(i  in 1..36) {
            pillEntityList.forEach {
                val pillTakenEntity : PillTakenEntity = PillTakenEntity(0L, someDayAtTheMorning)
                pillTakenEntity.pillEntity.target = it
                PillTakenDAOImpl.insert(
                    pillTakenEntity
                )
            }
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }

        val allList : List<PillTakenEntity> = PillTakenDAOImpl.getAll()
        Assert.assertEquals(72, allList.size)
        val allTakenPill : List<PillTakenEntity> = PillTakenDAOImpl.getAllByPill(pillEntityList.first())
        Assert.assertEquals(36, allTakenPill.size)
        val allTakenPillLimited : List<PillTakenEntity> = PillTakenDAOImpl.getPillTakenList(pillEntityList.first(), 0L, 20L)
        Assert.assertEquals(20, allTakenPillLimited.size)
        val allTakenPillLimitedPair : Pair<List<PillTakenEntity>,Float?> = PillTakenDAOImpl.getPillTaken(pillEntityList.first(), 0L, 20L)
        Assert.assertEquals(20, allTakenPillLimitedPair.first.size)
        Assert.assertEquals(488818.0f, allTakenPillLimitedPair.second)


    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        PillTakenDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)
        val pillEntityList : List<PillEntity> = PillDAOImpl.getPills()

        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for(i  in 1..36) {
            pillEntityList.forEach {
                val pillTakenEntity : PillTakenEntity = PillTakenEntity(0L, someDayAtTheMorning)
                pillTakenEntity.pillEntity.target = it
                PillTakenDAOImpl.insert(
                    pillTakenEntity
                )
            }
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }

        val touch = coin(0, pillEntityList.size - 1)
        pillEntityList.forEachIndexed { index, pillEntity : PillEntity ->
            if( index != touch) {
                PillTakenDAOImpl.deleteByPill(pillEntity)
                PillDAOImpl.delete(pillEntity)
            } else {
                val listTaken = PillTakenDAOImpl.getAllByPill(pillEntity)
                val touchTaken = coin(0, listTaken.size - 1)
                listTaken.forEachIndexed { indexTaken, taken : PillTakenEntity ->
                    if( indexTaken != touchTaken) {
                        PillTakenDAOImpl.delete(taken)
                    }
                }
            }
        }

        val allList : List<PillTakenEntity> = PillTakenDAOImpl.getAll()
        Assert.assertEquals(1, allList.size)
        val allTakenPillFirst : List<PillTakenEntity> = PillTakenDAOImpl.getAllByPill(pillEntityList.first())
        val allTakenPillSecond : List<PillTakenEntity> = PillTakenDAOImpl.getAllByPill(pillEntityList[1])

        Assert.assertEquals(1, (allTakenPillFirst.size + allTakenPillSecond.size) )

        val allTakenPillLimitedFirst : List<PillTakenEntity> = PillTakenDAOImpl.getPillTakenList(pillEntityList.first(), 0L, 20L)
        val allTakenPillLimitedScond : List<PillTakenEntity> = PillTakenDAOImpl.getPillTakenList(pillEntityList[1], 0L, 20L)

        Assert.assertEquals(1, (allTakenPillLimitedFirst.size + allTakenPillLimitedScond.size) )

        val allTakenPillLimitedPairFirst : Pair<List<PillTakenEntity>,Float?> = PillTakenDAOImpl.getPillTaken(pillEntityList.first(), 0L, 20L)
        val allTakenPillLimitedPairSecond : Pair<List<PillTakenEntity>,Float?> = PillTakenDAOImpl.getPillTaken(pillEntityList[1], 0L, 20L)

        Assert.assertEquals(1, (allTakenPillLimitedPairFirst.first.size + allTakenPillLimitedPairSecond.first.size) )

        if (allTakenPillLimitedPairFirst.first.size == 1) {
            Assert.assertEquals(null, allTakenPillLimitedPairSecond.second )
            val firstTake: Float? = TimeConverter.convertISOToHours(allTakenPillLimitedPairFirst.first.firstOrNull()?.getTime())
            Assert.assertEquals(firstTake, allTakenPillLimitedPairFirst.second  )

        }
        if (allTakenPillLimitedPairSecond.first.size == 1) {
            Assert.assertEquals(null, allTakenPillLimitedPairFirst.second )
            val firstTake: Float? = TimeConverter.convertISOToHours(allTakenPillLimitedPairSecond.first.firstOrNull()?.getTime())
            Assert.assertEquals(firstTake, allTakenPillLimitedPairSecond.second)
        }


    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}