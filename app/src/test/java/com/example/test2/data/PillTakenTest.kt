package com.example.test2.data

import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.dao.implementations.PillDAO
import com.example.test2.data.dao.implementations.PillTakenDAO
import com.example.test2.MyObjectBox
import com.example.test2.data.entities.implementations.Pill
import com.example.test2.data.entities.implementations.PillTaken
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


open class PillTakenTest {

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

        val mPillBox: Box<Pill> = store.boxFor(Pill::class.java)
        PillDAO.initialize(mPillBox)
        val mPillTakenBox: Box<PillTaken> = store.boxFor(PillTaken::class.java)
        PillTakenDAO.initialize(mPillTakenBox)

    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        val supradin: Pill = Pill(0L, "Supradin Forte")
        val totalMagneciano: Pill = Pill(0L, "Total Magneciano")
        PillDAO.insert(supradin)
        PillDAO.insert(totalMagneciano)
        val pillList : List<Pill> = PillDAO.getPills()


        val allList : List<PillTaken> = PillTakenDAO.getAll()
        Assert.assertEquals(0, allList.size)
        val allTakenPill : List<PillTaken> = PillTakenDAO.getAllByPill(pillList.first())
        Assert.assertEquals(0, allTakenPill.size)
        val allTakenPillLimited : List<PillTaken> = PillTakenDAO.getPillTakenList(pillList.first(), 0L, 20L)
        Assert.assertEquals(0, allTakenPillLimited.size)
        val allTakenPillLimitedPair : Pair<List<PillTaken>,Float?> = PillTakenDAO.getPillTaken(pillList.first(), 0L, 20L)
        Assert.assertEquals(0, allTakenPillLimitedPair.first.size)
        Assert.assertEquals(null, allTakenPillLimitedPair.second)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        val supradin: Pill = Pill(0L, "Supradin Forte")
        val totalMagneciano: Pill = Pill(0L, "Total Magneciano")
        PillDAO.insert(supradin)
        PillDAO.insert(totalMagneciano)
        val pillList : List<Pill> = PillDAO.getPills()

        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for(i  in 1..36) {
            pillList.forEach {
                val pillTaken : PillTaken = PillTaken(0L, someDayAtTheMorning)
                pillTaken.pill.target = it
                PillTakenDAO.insert(
                    pillTaken
                )
            }
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }

        val allList : List<PillTaken> = PillTakenDAO.getAll()
        Assert.assertEquals(72, allList.size)
        val allTakenPill : List<PillTaken> = PillTakenDAO.getAllByPill(pillList.first())
        Assert.assertEquals(36, allTakenPill.size)
        val allTakenPillLimited : List<PillTaken> = PillTakenDAO.getPillTakenList(pillList.first(), 0L, 20L)
        Assert.assertEquals(20, allTakenPillLimited.size)
        val allTakenPillLimitedPair : Pair<List<PillTaken>,Float?> = PillTakenDAO.getPillTaken(pillList.first(), 0L, 20L)
        Assert.assertEquals(20, allTakenPillLimitedPair.first.size)
        Assert.assertEquals(488818.0f, allTakenPillLimitedPair.second)


    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        PillTakenDAO.deleteAll()
    }

    @Test
    fun sparse() {
        val supradin: Pill = Pill(0L, "Supradin Forte")
        val totalMagneciano: Pill = Pill(0L, "Total Magneciano")
        PillDAO.insert(supradin)
        PillDAO.insert(totalMagneciano)
        val pillList : List<Pill> = PillDAO.getPills()

        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for(i  in 1..36) {
            pillList.forEach {
                val pillTaken : PillTaken = PillTaken(0L, someDayAtTheMorning)
                pillTaken.pill.target = it
                PillTakenDAO.insert(
                    pillTaken
                )
            }
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }

        val touch = coin(0, pillList.size - 1)
        pillList.forEachIndexed { index, pill : Pill ->
            if( index != touch) {
                PillTakenDAO.deleteByPill(pill)
                PillDAO.delete(pill)
            } else {
                val listTaken = PillTakenDAO.getAllByPill(pill)
                val touchTaken = coin(0, listTaken.size - 1)
                listTaken.forEachIndexed { indexTaken, taken : PillTaken ->
                    if( indexTaken != touchTaken) {
                        PillTakenDAO.delete(taken)
                    }
                }
            }
        }

        val allList : List<PillTaken> = PillTakenDAO.getAll()
        Assert.assertEquals(1, allList.size)
        val allTakenPillFirst : List<PillTaken> = PillTakenDAO.getAllByPill(pillList.first())
        val allTakenPillSecond : List<PillTaken> = PillTakenDAO.getAllByPill(pillList[1])

        Assert.assertEquals(1, (allTakenPillFirst.size + allTakenPillSecond.size) )

        val allTakenPillLimitedFirst : List<PillTaken> = PillTakenDAO.getPillTakenList(pillList.first(), 0L, 20L)
        val allTakenPillLimitedScond : List<PillTaken> = PillTakenDAO.getPillTakenList(pillList[1], 0L, 20L)

        Assert.assertEquals(1, (allTakenPillLimitedFirst.size + allTakenPillLimitedScond.size) )

        val allTakenPillLimitedPairFirst : Pair<List<PillTaken>,Float?> = PillTakenDAO.getPillTaken(pillList.first(), 0L, 20L)
        val allTakenPillLimitedPairSecond : Pair<List<PillTaken>,Float?> = PillTakenDAO.getPillTaken(pillList[1], 0L, 20L)

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