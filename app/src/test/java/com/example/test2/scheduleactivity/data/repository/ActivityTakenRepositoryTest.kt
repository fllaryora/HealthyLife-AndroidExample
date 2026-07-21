package com.example.test2.scheduleactivity.data.repository

import com.example.test2.data.converter.TimeConverter
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepositoryImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAOImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.recordactivity.data.repository.ActivityTakenRepositoryImpl
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
import org.junit.Assert.assertNotEquals
import java.time.OffsetDateTime
import java.time.ZoneOffset

open class ActivityTakenRepositoryTest {

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

        val mDailyActivityEntityBox: Box<DailyActivityEntity> = store.boxFor(DailyActivityEntity::class.java)
        ActivityDAOImpl.initialize(mDailyActivityEntityBox)
        ActivityRepositoryImpl.initialize(ActivityDAOImpl, testDispatcher)

        val mActivityTakenEntityBox: Box<ActivityTakenEntity> = store.boxFor(ActivityTakenEntity::class.java)
        ActivityTakenDAOImpl.initialize(mActivityTakenEntityBox)
        ActivityTakenRepositoryImpl.initialize(ActivityTakenDAOImpl, testDispatcher)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    //TEST OK
@OptIn(ExperimentalCoroutinesApi::class)
@Test
fun insert_and_deleteAll_should_emit_changes_after_insert_and_emit_after_deleteAll() = testScope.runTest {


    val activityEntity : DailyActivityEntity= DailyActivityEntity(
        0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
        TypeofRecorder.NONE.value
    )
    val newReturnedId : Long = ActivityRepositoryImpl.insert(
        activityEntity
    )
    advanceUntilIdle()
    activityEntity.id = newReturnedId

    val emissions = mutableListOf<List<ActivityTakenEntity>>()
    val secondEmission = CompletableDeferred<Unit>()
    val thirdEmission = CompletableDeferred<Unit>()

    val collectorJob = testScope.launch {

        ActivityTakenRepositoryImpl
            .getAll()
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

    val fixedTime: OffsetDateTime = OffsetDateTime.of(
        2022, 10, 25, 14, 30, 0, 0,
        ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
    )

    val pillTakenEntity = ActivityTakenEntity.create(activityEntityAsociated = activityEntity, date = fixedTime, )

    ActivityTakenRepositoryImpl.insert(pillTakenEntity)
    advanceUntilIdle()
    secondEmission.await()
    ActivityTakenRepositoryImpl.deleteAll()
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
        fixedTime,
        emissions[1][0].date //target will do not work after delete.
    )

    assertEquals(
        0,
        emissions[2].size
    )
}

    //TEST OK
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_delete_should_emit_changes_after_insert_and_emit_after_delete() = testScope.runTest {


        val activityEntity : DailyActivityEntity= DailyActivityEntity(
            0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
            TypeofRecorder.NONE.value
        )
        
        val newReturnedId : Long = ActivityRepositoryImpl.insert(
            activityEntity
        )
        advanceUntilIdle()
        activityEntity.id = newReturnedId

        val emissions = mutableListOf<List<ActivityTakenEntity>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {

            ActivityTakenRepositoryImpl
                .getAll()
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

        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2023, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )

        val pillTakenEntity = ActivityTakenEntity.create(activityEntityAsociated = activityEntity, date = fixedTime, )

        val newReturnedId2 : Long = ActivityTakenRepositoryImpl.insert(
            pillTakenEntity
        )
        advanceUntilIdle()
        pillTakenEntity.id = newReturnedId2
        secondEmission.await()
        ActivityTakenRepositoryImpl.delete(pillTakenEntity)
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
            fixedTime,
            emissions[1][0].date //target will do not work after delete.
        )

        assertEquals(
            0,
            emissions[2].size
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_getAllByPill_should_emit_changes() = testScope.runTest {


        val expectedName = "Vitaminas"
        val activityEntity : DailyActivityEntity= DailyActivityEntity(
            0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
            TypeofRecorder.NONE.value
        )
        val newReturnedId : Long =  ActivityRepositoryImpl.insert(
            activityEntity
        )
        advanceUntilIdle()
        activityEntity.id = newReturnedId


        val emissions = mutableListOf<List<ActivityTakenEntity>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {
            ActivityTakenRepositoryImpl
                .getAllByActivity(activityEntity)
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

        val fixedTime1: OffsetDateTime = OffsetDateTime.of(
            2024, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val fixedTime2: OffsetDateTime = OffsetDateTime.of(
            2024, 10, 26, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )

        val pillTakenEntity1 = ActivityTakenEntity.create(activityEntityAsociated = activityEntity, date = fixedTime1, )

        val pillTakenEntity2 = ActivityTakenEntity.create(activityEntityAsociated = activityEntity, date = fixedTime2, )

        val newReturnedId3 : Long =  ActivityTakenRepositoryImpl.insert(
            pillTakenEntity1
        )
        advanceUntilIdle()
        pillTakenEntity1.id = newReturnedId3
        secondEmission.await()
        val newReturnedId4 : Long =   ActivityTakenRepositoryImpl.insert(
            pillTakenEntity2
        )
        advanceUntilIdle()
        pillTakenEntity2.id = newReturnedId4
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
            fixedTime1,
            emissions[1][0].date //target will do not work after delete.
        )

        assertEquals(
            2,
            emissions[2].size
        )

    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_deleteByPill_should_emit_changes() = testScope.runTest {
        
        val activityEntity : DailyActivityEntity= DailyActivityEntity(
            0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
            TypeofRecorder.NONE.value
        )
        activityEntity.id = ActivityRepositoryImpl.insert(
            activityEntity
        )
        advanceUntilIdle()

        val emissions = mutableListOf<List<ActivityTakenEntity>>()
        val fourthEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {

            ActivityTakenRepositoryImpl
                .getAll()
                .collect { list ->

                    emissions.add(list)
                    println("Emission ${emissions.size}")
                    if (emissions.size == 2) {
                        println("Emission ${emissions[1]}")
                    }
                    if (emissions.size == 3) {
                        println("Emission ${emissions[2]}")
                    }
                    if (emissions.size == 4) {
                        println("Emission ${emissions[3]}")
                    }
                    when (emissions.size) {
                        3 -> thirdEmission.complete(Unit)
                        4 -> fourthEmission.complete(Unit)
                    }


                    if (emissions.size == 4) {
                        cancel()
                    }
                }
        }
        advanceUntilIdle()

        val fixedTime1: OffsetDateTime = OffsetDateTime.of(
            2021, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val fixedTime2: OffsetDateTime = OffsetDateTime.of(
            2021, 10, 26, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )

        val pillTakenEntity1 = ActivityTakenEntity.create(date = fixedTime1, activityEntityAsociated =  activityEntity)
        val pillTakenEntity2 = ActivityTakenEntity.create(date = fixedTime2, activityEntityAsociated =  activityEntity)


        pillTakenEntity1.id = ActivityTakenRepositoryImpl.insert(pillTakenEntity1)
        advanceUntilIdle()

        pillTakenEntity2.id = ActivityTakenRepositoryImpl.insert(pillTakenEntity2)
        advanceUntilIdle()
        thirdEmission.await()
        println("Deleting by activityEntity...")
        ActivityTakenRepositoryImpl.deleteByActivity(activityEntity)
        advanceUntilIdle()
        fourthEmission.await()
        collectorJob.join()

        assertEquals(4, emissions.size)

        assertEquals(
            0,
            emissions[0].size
        )

        assertEquals(
            1,
            emissions[1].size
        )

        assertEquals(
            1L,
            emissions[1][0].activity.target.id
        )

        assertEquals(
            fixedTime1,
            emissions[1][0].date
        )

        assertEquals(
            2,
            emissions[2].size
        )

        assertEquals(
            fixedTime2,
            emissions[2][1].date //target will do not work after delete.
        )

        assertEquals(
            0,
            emissions[3].size
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_getPaginated_should_emit_changes() = testScope.runTest {

        val activityEntity : DailyActivityEntity= DailyActivityEntity(
            0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
            TypeofRecorder.NONE.value
        )
        val newReturnedId : Long =  ActivityRepositoryImpl.insert(
            activityEntity
        )
        advanceUntilIdle()
        activityEntity.id = newReturnedId

        val emissions = mutableListOf<List<ActivityTakenEntity>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {
            ActivityTakenRepositoryImpl
                .getActivityTakenList(activityEntity, 0L, 20L)
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

        val fixedTime1: OffsetDateTime = OffsetDateTime.of(
            2024, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val fixedTime2: OffsetDateTime = OffsetDateTime.of(
            2024, 10, 26, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )

        val pillTakenEntity1 = ActivityTakenEntity.create(activityEntityAsociated = activityEntity, date = fixedTime1, )

        val pillTakenEntity2 = ActivityTakenEntity.create(activityEntityAsociated = activityEntity, date = fixedTime2, )

        val newReturnedId3 : Long =  ActivityTakenRepositoryImpl.insert(
            pillTakenEntity1
        )
        advanceUntilIdle()
        pillTakenEntity1.id = newReturnedId3
        secondEmission.await()
        val newReturnedId4 : Long =   ActivityTakenRepositoryImpl.insert(
            pillTakenEntity2
        )
        advanceUntilIdle()
        pillTakenEntity2.id = newReturnedId4
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
            fixedTime1,
            emissions[1][0].date //target will do not work after delete.
        )

        assertEquals(
            2,
            emissions[2].size
        )

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_getPairPaginated_should_emit_changes() = testScope.runTest {

        val activityEntity : DailyActivityEntity= DailyActivityEntity(
            0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
            TypeofRecorder.NONE.value
        )
        val newReturnedId : Long =  ActivityRepositoryImpl.insert(
            activityEntity
        )
        advanceUntilIdle()
        activityEntity.id = newReturnedId

        val emissions = mutableListOf<Pair<List<ActivityTakenEntity>, Float?>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {
            ActivityTakenRepositoryImpl
                .getActivityTaken(activityEntity, 0L, 20L)
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

        val fixedTime1: OffsetDateTime = OffsetDateTime.of(
            2024, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val fixedTime2: OffsetDateTime = OffsetDateTime.of(
            2024, 10, 26, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )

        val pillTakenEntity1 = ActivityTakenEntity.create(activityEntityAsociated = activityEntity, date = fixedTime1, )

        val pillTakenEntity2 = ActivityTakenEntity.create(activityEntityAsociated = activityEntity, date = fixedTime2, )

        val newReturnedId3 : Long =  ActivityTakenRepositoryImpl.insert(
            pillTakenEntity1
        )
        advanceUntilIdle()
        pillTakenEntity1.id = newReturnedId3
        secondEmission.await()
        val newReturnedId4 : Long =   ActivityTakenRepositoryImpl.insert(
            pillTakenEntity2
        )
        advanceUntilIdle()
        pillTakenEntity2.id = newReturnedId4
        thirdEmission.await()

        collectorJob.join()

        assertEquals(3, emissions.size)

        assertEquals(
            0,
            emissions[0].first.size
        )

        assertEquals(
            null,
            emissions[0].second
        )

        assertEquals(
            1,
            emissions[1].first.size
        )

        assertEquals(
            fixedTime1,
            emissions[1].first[0].date //target will do not work after delete.
        )

        val firstTake: Float? = TimeConverter.convertISOToHours(pillTakenEntity1.getTime())
        assertEquals(
            firstTake,
            emissions[1].second //target will do not work after delete.
        )


        assertEquals(
            2,
            emissions[2].first.size
        )

        val secondTake: Float? = TimeConverter.convertISOToHours(pillTakenEntity2.getTime())
        assertNotEquals(
            secondTake,
            emissions[2].second //target will do not work after delete.
        )

        assertEquals(
            firstTake,
            emissions[2].second //target will do not work after delete.
        )

    }
    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}