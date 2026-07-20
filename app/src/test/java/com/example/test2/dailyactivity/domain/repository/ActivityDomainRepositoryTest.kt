package com.example.test2.dailyactivity.domain.repository

import com.example.test2.features.MyObjectBox
import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepositoryImpl
import com.example.test2.features.dailyactivity.domain.repository.ActivityUseCaseRepositoryImpl
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.OffsetDateTime
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertNotEquals

open class ActivityDomainRepositoryTest {

    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!


    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

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

        val mActivityEntityBox: Box<DailyActivityEntity> = store.boxFor(DailyActivityEntity::class.java)
        ActivityDAOImpl.initialize(mActivityEntityBox)
        ActivityRepositoryImpl.initialize(ActivityDAOImpl, testDispatcher)
        ActivityUseCaseRepositoryImpl.initialize(ActivityRepositoryImpl, testDispatcher)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

@OptIn(ExperimentalCoroutinesApi::class)
@Test
fun insert_and_deleteAll_should_emit_changes_after_insert_and_emit_after_deleteAll() = testScope.runTest {

    val emissionsWEDNESDAY = mutableListOf<DailyActivityEntity?>()
    val emissionsSUNDAY = mutableListOf<DailyActivityEntity?>()
    val emissionsTHURSDAY = mutableListOf<DailyActivityEntity?>()

    val collectorJobWEDNESDAY = testScope.launch {
        ActivityUseCaseRepositoryImpl
            .getNextActivity(17,55, DaysOfWeekEnum.WEDNESDAY)
            .collect { nextActivity: DailyActivityEntity? ->
                emissionsWEDNESDAY.add(nextActivity)
                if (emissionsWEDNESDAY.size == 7) {
                    cancel()
                }
            }
    }
    advanceUntilIdle()
    val collectorJobSUNDAY = testScope.launch {
        ActivityUseCaseRepositoryImpl
            .getNextActivity(17,55, DaysOfWeekEnum.SUNDAY)
            .collect { nextActivity: DailyActivityEntity? ->
                emissionsSUNDAY.add(nextActivity)
                if (emissionsSUNDAY.size == 7) {
                    cancel()
                }
            }
    }
    advanceUntilIdle()
    val collectorJobTHURSDAY = testScope.launch {
        ActivityUseCaseRepositoryImpl
            .getNextActivity(17,55, DaysOfWeekEnum.THURSDAY)
            .collect { nextActivity: DailyActivityEntity? ->
                emissionsTHURSDAY.add(nextActivity)
                if (emissionsTHURSDAY.size == 7) {
                    cancel()
                }
            }
    }
    advanceUntilIdle()
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
        ActivityRepositoryImpl.insert(it)
    }

    advanceUntilIdle()

    collectorJobWEDNESDAY.join()
    collectorJobSUNDAY.join()
    collectorJobTHURSDAY.join()
    assertEquals(7, emissionsWEDNESDAY.size)
    assertEquals(7, emissionsSUNDAY.size)
    assertEquals(7, emissionsTHURSDAY.size)

    assertEquals(
        null,
        emissionsWEDNESDAY[0]
    )
    assertEquals(
        null,
        emissionsSUNDAY[0]
    )

    assertEquals(
        null,
        emissionsTHURSDAY[0]
    )

    assertNotEquals(
        null,
        emissionsWEDNESDAY[6]
    )
    assertNotEquals(
        null,
        emissionsSUNDAY[6]
    )

    assertNotEquals(
        null,
        emissionsTHURSDAY[6]
    )

    assertEquals(
        "Actividad fisica",
        emissionsWEDNESDAY[6]?.name
    )
    assertEquals(
        "Cenar" ,
        emissionsSUNDAY[6]?.name
    )

    assertEquals(
        "Desayunar",
        emissionsTHURSDAY[6]?.name
    )


}


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_delete_should_emit_changes_after_insert_and_emit_after_delete() = testScope.runTest {

        val current: OffsetDateTime = OffsetDateTime.now()
        val currentDayOfWeek: DaysOfWeekEnum = DaysOfWeekEnum.fromDayOfWeek(current.dayOfWeek)
        val emissionsTODAY = mutableListOf<DailyActivityEntity?>()


        val collectorJobTODAY = testScope.launch {
            ActivityUseCaseRepositoryImpl
                .getNextActivity(17,55, currentDayOfWeek)
                .collect { nextActivity: DailyActivityEntity? ->
                    emissionsTODAY.add(nextActivity)
                    if (emissionsTODAY.size == 7) {
                        cancel()
                    }
                }
        }


        advanceUntilIdle()
        val dailyActivities: List<DailyActivityEntity> = listOf(
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

        dailyActivities.forEach {
            ActivityRepositoryImpl.insert(it)
        }

        advanceUntilIdle()

        collectorJobTODAY.join()
        assertEquals(7, emissionsTODAY.size)


        assertEquals(
            null,
            emissionsTODAY[0]
        )
        assertNotEquals(
            null,
            emissionsTODAY[6]
        )


        assertEquals(
            "Actividad fisica",
            emissionsTODAY[6]?.name
        )

    }


    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}