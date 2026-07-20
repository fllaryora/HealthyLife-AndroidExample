package com.example.test2.dailyactivity.domain

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.domain.ActivityUseCaseImpl
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.OffsetDateTime
import kotlin.random.Random

open class DailyActivitDomainTest {

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

        val mPillBox: Box<DailyActivityEntity> = store.boxFor(DailyActivityEntity::class.java)
        ActivityDAOImpl.initialize(mPillBox)
        ActivityUseCaseImpl.initialize(ActivityDAOImpl)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
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

        val nextAct = ActivityUseCaseImpl.getNextActivity(17,55, DaysOfWeekEnum.WEDNESDAY)
        assert(nextAct != null)
        assert("Actividad fisica" == nextAct!!.name)
        val nextAct2 = ActivityUseCaseImpl.getNextActivity(17,55, DaysOfWeekEnum.SUNDAY)
        assert(nextAct2 != null)
        assert("Cenar" == nextAct2!!.name)
        val nextAct3 = ActivityUseCaseImpl.getNextActivity(17,55, DaysOfWeekEnum.THURSDAY)
        assert(nextAct3 != null)
        assert("Desayunar" == nextAct3!!.name)

    }

    @Test
    fun sparse() {
        //Its not in order to check the order.
        val daylyActivities: List<DailyActivityEntity> = listOf(
            DailyActivityEntity(
                0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Desayunar", 9, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Cenar", 21, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Almorzar", 13, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Merendar", 17, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Actividad fisica", 18, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
        )

        val current: OffsetDateTime = OffsetDateTime.now()
        val currentDayOfWeek: DaysOfWeekEnum = DaysOfWeekEnum.fromDayOfWeek(current.dayOfWeek)
        daylyActivities.forEach {
            ActivityDAOImpl.insert(it)
        }
        val nextAct = ActivityUseCaseImpl.getNextActivity(17,55, currentDayOfWeek)
        assert(nextAct != null)
        assert("Actividad fisica" == nextAct!!.name)

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}