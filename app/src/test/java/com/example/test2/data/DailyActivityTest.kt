package com.example.test2.data

import com.example.test2.data.dao.implementations.ActivityDAO
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.implementations.DailyActivity
import com.example.test2.MyObjectBox
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
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.entities.enums.TypeofRecorder.NONE
import java.time.OffsetDateTime


open class DailyActivityTest {

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

        val mPillBox: Box<DailyActivity> = store.boxFor(DailyActivity::class.java)
        ActivityDAO.initialize(mPillBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        val allList : List<DailyActivity> = ActivityDAO.getActivities()
        Assert.assertEquals(0, allList.size)

        assert(ActivityDAO.getNextActivityFromList(0,0, DaysOfWeekEnum.ALL,emptyList<DailyActivity>()) == null)
        assert(ActivityDAO.getNextActivity(0,0, DaysOfWeekEnum.ALL) == null)

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


        val allList : List<DailyActivity> = ActivityDAO.getActivities()
        Assert.assertEquals(6, allList.size)

        val nextAct = ActivityDAO.getNextActivity(17,55, DaysOfWeekEnum.WEDNESDAY)
        assert(nextAct != null)
        assert("Actividad fisica" == nextAct!!.name)
        val nextAct2 = ActivityDAO.getNextActivity(17,55, DaysOfWeekEnum.SUNDAY)
        assert(nextAct2 != null)
        assert("Cenar" == nextAct2!!.name)
        val nextAct3 = ActivityDAO.getNextActivity(17,55, DaysOfWeekEnum.THURSDAY)
        assert(nextAct3 != null)
        assert("Desayunar" == nextAct3!!.name)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        ActivityDAO.deleteAll()
    }

    @Test
    fun sparse() {
        //Its not in order to check the order.
        val daylyActivities: List<DailyActivity> = listOf(
            DailyActivity(0L, "Pesarse",8,0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value),
            DailyActivity(0L, "Desayunar",9,0,daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value),
            DailyActivity(0L, "Cenar",21,0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value),
            DailyActivity(0L, "Almorzar",13,0,daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value),
            DailyActivity(0L, "Merendar", 17,0,daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value),
            DailyActivity(0L, "Actividad fisica",18,0,daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value),
        )
        val current = OffsetDateTime.now()
        val currentDayOfWeek: DaysOfWeekEnum = DaysOfWeekEnum.fromDayOfWeek(current.dayOfWeek)
        daylyActivities.forEach {
            ActivityDAO.insert(it)
        }
        val nextAct = ActivityDAO.getNextActivity(17,55, currentDayOfWeek)
        assert(nextAct != null)
        assert("Actividad fisica" == nextAct!!.name)

        var allList = ActivityDAO.getActivities()
        val touch : Int  = coin(0, allList.size-1)
        allList.forEachIndexed { index, weight : DailyActivity ->
            if( index != touch){
                ActivityDAO.delete(weight)
            }
        }

        allList  = ActivityDAO.getActivities()
        Assert.assertEquals(1, allList.size)


    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}