package com.example.test2.dailyactivity.ui.viewmodel

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepositoryImpl
import com.example.test2.features.dailyactivity.ui.viewmodel.ActivityViewModel
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

open class ActivityViewModelTest {

    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!


    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: ActivityViewModel
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
        ActivityRepositoryImpl.initialize(ActivityDAOImpl, Dispatchers.IO)
        viewModel = ActivityViewModel(ActivityRepositoryImpl)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

@OptIn(ExperimentalCoroutinesApi::class)
@Test
fun addActivity_should_update_activities_flow() = testScope.runTest {

    val emissions = mutableListOf<List<DailyActivityEntity>>()
    val collectorJob = testScope.launch {

        viewModel.activities
            .collect { list ->

                emissions.add(list)

                if (emissions.size == 2) {
                    cancel()
                }
            }
    }
    advanceUntilIdle()
    // Arrange
    val activityName = "Gym"
    val hour = 8
    val minute = 30
    val days = setOf(
        DaysOfWeekEnum.MONDAY,
        DaysOfWeekEnum.WEDNESDAY
    )

    val recorderType = TypeofRecorder.WEIGHT_RECORDER

    // Act
    viewModel.addActivity(
        activityName = activityName,
        activityHour = hour,
        activityMinute = minute,
        selectedDays = days,
        activityTypeOfRecorder = recorderType,
        activityIsAlarmEnabled = true
    )

    advanceUntilIdle()

    //it will wait until second emission (DispatcherIO has finished when this happens)
    collectorJob.join()

    assertEquals(
        2,
        emissions.size
    )
    assertEquals(
        0,
        emissions[0].size
    )

    assertEquals(
        1,
        emissions[1].size
    )

    assertEquals(
        activityName,
        emissions[1][0].name
    )

}


    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}