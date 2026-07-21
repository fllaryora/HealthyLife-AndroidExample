package com.example.test2.dailyactivity.data.repository

import com.example.test2.features.MyObjectBox
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepositoryImpl
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
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope

open class ActivityRepositoryTest {

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

    val emissions = mutableListOf<List<DailyActivityEntity>>()
    val secondEmission = CompletableDeferred<Unit>()
    val thirdEmission = CompletableDeferred<Unit>()

    val collectorJob = testScope.launch {

        ActivityRepositoryImpl
            .getActivities()
            .collect { list ->

                emissions.add(list)

                when (emissions.size) {
                    2 -> secondEmission.complete(Unit)
                    3 -> thirdEmission.complete(Unit)
                }

                if (emissions.size == 3) {
                    cancel()
                }
            }
    }
    advanceUntilIdle()

    ActivityRepositoryImpl.insert(
        DailyActivityEntity(
            0L, "Actividad fisica", 18, 0, daysOfWeek = DaysOfWeekEnum.WEDNESDAY.value,
            TypeofRecorder.NONE.value
        )
    )
    advanceUntilIdle()
    secondEmission.await()
    ActivityRepositoryImpl.deleteAll()
    advanceUntilIdle()
    thirdEmission.await()
    collectorJob.join()
    assertEquals(3, emissions.size)

    assertEquals(
        0,
        emissions[0].size
    )

    assertEquals(
        1,
        emissions[1].size
    )

    assertEquals(
        "Actividad fisica",
        emissions[1][0].name
    )

    assertEquals(
        0,
        emissions[2].size
    )
}

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_delete_should_emit_changes_after_insert_and_emit_after_delete() = testScope.runTest {

        val emissions = mutableListOf<List<DailyActivityEntity>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {

            ActivityRepositoryImpl
                .getActivities()
                .collect { list ->

                    emissions.add(list)

                    when (emissions.size) {
                        2 -> secondEmission.complete(Unit)
                        3 -> thirdEmission.complete(Unit)
                    }

                    if (emissions.size == 3) {
                        cancel()
                    }
                }
        }
        advanceUntilIdle()
        val entity : DailyActivityEntity= DailyActivityEntity(
            0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
            TypeofRecorder.NONE.value
        )

        ActivityRepositoryImpl.insert(
            entity
        )
        advanceUntilIdle()
        secondEmission.await()
        entity.id = 1L //little hack
        ActivityRepositoryImpl.delete(entity)
        advanceUntilIdle()
        thirdEmission.await()
        collectorJob.join()
        assertEquals(3, emissions.size)

        assertEquals(
            0,
            emissions[0].size
        )

        assertEquals(
            1,
            emissions[1].size
        )

        assertEquals(
            "Pesarse",
            emissions[1][0].name
        )

        assertEquals(
            0,
            emissions[2].size
        )
    }


    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}