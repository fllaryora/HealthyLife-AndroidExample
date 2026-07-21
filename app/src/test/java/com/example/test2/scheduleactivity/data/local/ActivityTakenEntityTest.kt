package com.example.test2.scheduleactivity.data.local

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAOImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
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

open class ActivityTakenEntityTest {

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

        val mActivityBox: Box<DailyActivityEntity> = store.boxFor(DailyActivityEntity::class.java)
        val mActivityTakenEntityBox: Box<ActivityTakenEntity> = store.boxFor(ActivityTakenEntity::class.java)
        ActivityDAOImpl.initialize(mActivityBox)
        ActivityTakenDAOImpl.initialize(mActivityTakenEntityBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {

        val allList : List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()
        Assert.assertEquals(0, allList.size)

        val activity = DailyActivityEntity(
            0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.MONDAY.value,
            TypeofRecorder.NONE.value
        )
        ActivityDAOImpl.insert(activity)
        val actList : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()

        var actT1 = ActivityTakenDAOImpl.getActivityTaken(actList.first(), 0, 20L)
        Assert.assertEquals(0, actT1.first.size)

        var actT2 = ActivityTakenDAOImpl.getActivityTakenList(actList.first(), 0, 20L)
        Assert.assertEquals(0, actT2.size)

        var actT3 = ActivityTakenDAOImpl.getAllByActivity(actList.first())
        Assert.assertEquals(0, actT3.size)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        //Its not in order to check the order.
        val dailyActivities: List<DailyActivityEntity> = listOf(
            DailyActivityEntity(
                0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.MONDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Desayunar", 9, 0, daysOfWeek = DaysOfWeekEnum.FRIDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Cenar", 21, 0, daysOfWeek = DaysOfWeekEnum.SUNDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Almorzar", 13, 0, daysOfWeek = DaysOfWeekEnum.TUESDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Merendar", 17, 0, daysOfWeek = DaysOfWeekEnum.THURSDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Actividad fisica", 18, 0, daysOfWeek = DaysOfWeekEnum.WEDNESDAY.value,
                TypeofRecorder.NONE.value
            ),
        )
        dailyActivities.forEach {
            ActivityDAOImpl.insert(it)
        }


        val activitiesOfWeek : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()
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
                    val activityTakenEntity : ActivityTakenEntity =
                        ActivityTakenEntity.create(
                            date=  someDayAtTheMorning,
                            rating = randRating(),
                            activityEntityAsociated = it
                        )
                    ActivityTakenDAOImpl.insert(
                        activityTakenEntity
                    )
            }
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }
        //6 activities  in 36 days
        val allList2 : List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()
        Assert.assertEquals(36 * 6, allList2.size)

        val actList : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()

        var actT1 = ActivityTakenDAOImpl.getActivityTaken(actList.first(), 0, 20L)
        Assert.assertEquals(20, actT1.first.size)

        var actT2 = ActivityTakenDAOImpl.getActivityTakenList(actList.first(), 0, 20L)
        Assert.assertEquals(20, actT2.size)

        //1 activity  in 36 days
        var actT3 = ActivityTakenDAOImpl.getAllByActivity(actList.first())
        Assert.assertEquals(36, actT3.size)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        ActivityTakenDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {
        //Its not in order to check the order.
        val dailyActivities: List<DailyActivityEntity> = listOf(
            DailyActivityEntity(
                0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.MONDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Desayunar", 9, 0, daysOfWeek = DaysOfWeekEnum.FRIDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Cenar", 21, 0, daysOfWeek = DaysOfWeekEnum.SUNDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Almorzar", 13, 0, daysOfWeek = DaysOfWeekEnum.TUESDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Merendar", 17, 0, daysOfWeek = DaysOfWeekEnum.THURSDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Actividad fisica", 18, 0, daysOfWeek = DaysOfWeekEnum.WEDNESDAY.value,
                TypeofRecorder.NONE.value
            ),
        )
        dailyActivities.forEach {
            ActivityDAOImpl.insert(it)
        }


        val activities6OfWeek : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()
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
                val activityTakenEntity : ActivityTakenEntity =
                    ActivityTakenEntity.create(
                        date = someDayAtTheMorning,
                        rating = randRating(),
                        activityEntityAsociated = it
                    )
                ActivityTakenDAOImpl.insert(
                    activityTakenEntity
                )
            }
            someDayAtTheMorning = someDayAtTheMorning.plusDays(aDay)
                .withHour(7).withMinute(0)
        }

        // 6 activities , taken in 36 days
        var allActivitiesTaken : List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()
        Assert.assertEquals(36 * 6, allActivitiesTaken.size)
        ActivityTakenDAOImpl.delete(allActivitiesTaken.first())
        allActivitiesTaken  = ActivityTakenDAOImpl.getAll()
        // 6 activities , taken in 36 days , except one
        Assert.assertEquals((36 * 6) -1, allActivitiesTaken.size)

        ActivityTakenDAOImpl.deleteByActivity(allActivitiesTaken.first().activity.target)
        allActivitiesTaken  = ActivityTakenDAOImpl.getAll()
        // 5 activities , taken in 36 days , except one
        Assert.assertEquals((36 * 5) -1, allActivitiesTaken.size)
    }


    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}