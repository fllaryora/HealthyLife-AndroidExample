package com.example.test2.data

import com.example.test2.data.dao.implementations.ActivityDAO
import com.example.test2.data.dao.implementations.ActivityTakenDAO
import com.example.test2.data.entities.implementations.DailyActivity
import com.example.test2.data.entities.implementations.MyObjectBox
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.random.Random
import com.example.test2.data.entities.enums.DaysOfWeekEnum.FRIDAY
import com.example.test2.data.entities.enums.DaysOfWeekEnum.MONDAY
import com.example.test2.data.entities.enums.DaysOfWeekEnum.SUNDAY
import com.example.test2.data.entities.enums.DaysOfWeekEnum.THURSDAY
import com.example.test2.data.entities.enums.DaysOfWeekEnum.TUESDAY
import com.example.test2.data.entities.enums.DaysOfWeekEnum.WEDNESDAY
import com.example.test2.data.entities.enums.TypeofRecorder.NONE
import com.example.test2.data.entities.implementations.ActivityTaken
import java.time.OffsetDateTime
import java.time.ZoneOffset


open class ActivityTakenTest {

    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!

    private fun coin(from: Int, until: Int): Int {
        return Random(System.nanoTime()).nextInt(from = from, until = until)
    }

    private fun randTaken(): Boolean {
        return Random(System.nanoTime()).nextBoolean()
    }

    private fun randRating(): Int {
        return Random(System.nanoTime()).nextInt(0, 10)
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

        val mActivityBox: Box<DailyActivity> = store.boxFor(DailyActivity::class.java)
        val mActivityTakenBox: Box<ActivityTaken> = store.boxFor(ActivityTaken::class.java)
        ActivityDAO.initialize(mActivityBox)
        ActivityTakenDAO.initialize(mActivityTakenBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {

        val allList : List<ActivityTaken> = ActivityTakenDAO.getAll()
        Assert.assertEquals(0, allList.size)

        val activity = DailyActivity(0L, "Pesarse",8,0, daysOfWeek = MONDAY.value,
            NONE.value)
        ActivityDAO.insert(activity)
        val actList : List<DailyActivity> = ActivityDAO.getActivities()

        var actT1 = ActivityTakenDAO.getActivityTaken(actList.first(), 0, 20L)
        Assert.assertEquals(0, actT1.first.size)

        var actT2 = ActivityTakenDAO.getActivityTakenList(actList.first(), 0, 20L)
        Assert.assertEquals(0, actT2.size)

        var actT3 = ActivityTakenDAO.getAllByActivity(actList.first())
        Assert.assertEquals(0, actT3.size)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        //Its not in order to check the order.
        val dailyActivities: List<DailyActivity> = listOf(
            DailyActivity(0L, "Pesarse",8,0, daysOfWeek = MONDAY.value,
                NONE.value),
            DailyActivity(0L, "Desayunar",9,0,daysOfWeek = FRIDAY.value,
                NONE.value),
            DailyActivity(0L, "Cenar",21,0, daysOfWeek = SUNDAY.value,
                NONE.value),
            DailyActivity(0L, "Almorzar",13,0,daysOfWeek = TUESDAY.value,
                NONE.value),
            DailyActivity(0L, "Merendar", 17,0,daysOfWeek = THURSDAY.value,
                NONE.value),
            DailyActivity(0L, "Actividad fisica",18,0,daysOfWeek = WEDNESDAY.value,
                NONE.value),
        )
        dailyActivities.forEach {
            ActivityDAO.insert(it)
        }


        val activitiesOfWeek : List<DailyActivity> = ActivityDAO.getActivities()
        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
            .minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for(i  in 1..36) {
            activitiesOfWeek.forEach {
                    val activityTaken : ActivityTaken =
                        ActivityTaken(0L, someDayAtTheMorning,
                            randRating()
                        )
                    activityTaken.activity.target = it
                    ActivityTakenDAO.insert(
                        activityTaken
                    )
            }
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }
        //6 activities  in 36 days
        val allList2 : List<ActivityTaken> = ActivityTakenDAO.getAll()
        Assert.assertEquals(36 * 6, allList2.size)

        val actList : List<DailyActivity> = ActivityDAO.getActivities()

        var actT1 = ActivityTakenDAO.getActivityTaken(actList.first(), 0, 20L)
        Assert.assertEquals(20, actT1.first.size)

        var actT2 = ActivityTakenDAO.getActivityTakenList(actList.first(), 0, 20L)
        Assert.assertEquals(20, actT2.size)

        //1 activity  in 36 days
        var actT3 = ActivityTakenDAO.getAllByActivity(actList.first())
        Assert.assertEquals(36, actT3.size)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        ActivityTakenDAO.deleteAll()
    }

    @Test
    fun sparse() {
        //Its not in order to check the order.
        val dailyActivities: List<DailyActivity> = listOf(
            DailyActivity(0L, "Pesarse",8,0, daysOfWeek = MONDAY.value,
                NONE.value),
            DailyActivity(0L, "Desayunar",9,0,daysOfWeek = FRIDAY.value,
                NONE.value),
            DailyActivity(0L, "Cenar",21,0, daysOfWeek = SUNDAY.value,
                NONE.value),
            DailyActivity(0L, "Almorzar",13,0,daysOfWeek = TUESDAY.value,
                NONE.value),
            DailyActivity(0L, "Merendar", 17,0,daysOfWeek = THURSDAY.value,
                NONE.value),
            DailyActivity(0L, "Actividad fisica",18,0,daysOfWeek = WEDNESDAY.value,
                NONE.value),
        )
        dailyActivities.forEach {
            ActivityDAO.insert(it)
        }


        val activities6OfWeek : List<DailyActivity> = ActivityDAO.getActivities()
        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val aDay = 1L
        val sinceAYearAgoAtTheMorning: OffsetDateTime = fixedTime.minusDays(35L).withHour(7).withMinute(0)
            .minusDays(35L).withHour(7).withMinute(0)
        var someDayAtTheMorning: OffsetDateTime = sinceAYearAgoAtTheMorning
        for(i  in 1..36) {
            activities6OfWeek.forEach {
                val activityTaken : ActivityTaken =
                    ActivityTaken(0L, someDayAtTheMorning,
                        randRating()
                    )
                activityTaken.activity.target = it
                ActivityTakenDAO.insert(
                    activityTaken
                )
            }
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }

        // 6 activities , taken in 36 days
        var allActivitiesTaken : List<ActivityTaken> = ActivityTakenDAO.getAll()
        Assert.assertEquals(36 * 6, allActivitiesTaken.size)
        ActivityTakenDAO.delete(allActivitiesTaken.first())
        allActivitiesTaken  = ActivityTakenDAO.getAll()
        // 6 activities , taken in 36 days , except one
        Assert.assertEquals((36 * 6) -1, allActivitiesTaken.size)

        ActivityTakenDAO.deleteByActivity(allActivitiesTaken.first().activity.target)
        allActivitiesTaken  = ActivityTakenDAO.getAll()
        // 5 activities , taken in 36 days , except one
        Assert.assertEquals((36 * 5) -1, allActivitiesTaken.size)
    }


    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}