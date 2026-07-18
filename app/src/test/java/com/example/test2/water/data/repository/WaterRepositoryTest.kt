package com.example.test2.water.data.repository

import com.example.test2.features.MyObjectBox
import com.example.test2.data.converter.TimeConverter
import com.example.test2.features.water.data.local.WaterDAOImpl
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.water.data.repository.WaterRepositoryImpl
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
import java.time.ZoneOffset

open class WaterRepositoryTest {

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

        val mWaterEntityBox: Box<WaterEntity> = store.boxFor(WaterEntity::class.java)
        WaterDAOImpl.initialize(mWaterEntityBox)
        WaterRepositoryImpl.initialize(WaterDAOImpl, testDispatcher)
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

    val emissions = mutableListOf<List<WaterEntity>>()
    val secondEmission = CompletableDeferred<Unit>()
    val thirdEmission = CompletableDeferred<Unit>()

    val collectorJob = testScope.launch {

        WaterRepositoryImpl
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

    WaterRepositoryImpl.insert(
        WaterEntity(
            id = 0L,
            date = OffsetDateTime.now(),
            volume = 1.5f
        )
    )
    advanceUntilIdle()
    secondEmission.await()
    WaterRepositoryImpl.deleteAll()
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
        1.5f,
        emissions[1][0].volume
    )

    assertEquals(
        0,
        emissions[2].size
    )
}

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_delete_should_emit_changes_after_insert_and_emit_after_delete() = testScope.runTest {

        val emissions = mutableListOf<List<WaterEntity>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {

            WaterRepositoryImpl
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
        val entity : WaterEntity= WaterEntity(
            id = 0L,
            date = OffsetDateTime.now(),
            volume = 1.5f
        )

        WaterRepositoryImpl.insert(
            entity
        )
        advanceUntilIdle()
        secondEmission.await()
        entity.id = 1L //little hack
        WaterRepositoryImpl.delete(entity)
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
            1.5f,
            emissions[1][0].volume
        )

        assertEquals(
            0,
            emissions[2].size
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_should_emit_changes_after_insert_pagination_simple() = testScope.runTest {

        val emissions = mutableListOf<List<WaterEntity>>()
        val secondEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {

            WaterRepositoryImpl
                .getWaters(0L,20L)
                .collect { list ->

                    emissions.add(list)

                    when (emissions.size) {
                        2 -> secondEmission.complete(Unit)
                    }

                    if (emissions.size == 2) {
                        cancel()
                    }
                }
        }
        advanceUntilIdle()
        var entity : WaterEntity= WaterEntity(
            id = 0L,
            date = OffsetDateTime.now(),
            volume = 1.5f
        )

        WaterRepositoryImpl.insert(
            entity
        )
        advanceUntilIdle()
        secondEmission.await()
        collectorJob.join()
        assertEquals(2, emissions.size)

        assertEquals(
            0,
            emissions[0].size
        )

        assertEquals(
            1,
            emissions[1].size
        )

        assertEquals(
            1.5f,
            emissions[1][0].volume
        )

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_delete_should_emit_changes_after_insert_pagination_complex() = testScope.runTest {

        val emissions = mutableListOf<List<WaterEntity>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )

        val collectorJob = testScope.launch {

            WaterRepositoryImpl
                .getIntakesByDay(fixedTime)
                .collect { list: List<WaterEntity> ->

                    println(
                        "Emission #${emissions.size + 1} -> size=${list.size}"
                    )

                    emissions.add(list)

                    when (emissions.size) {
                        2 -> secondEmission.complete(Unit)
                        3 -> thirdEmission.complete(Unit)
                    }

                    if (emissions.size == 3) {
                        println("Second emission arrived")
                        cancel()
                    }
                }
        }
        advanceUntilIdle()
        val expectedFloat = TimeConverter.convertISOToHours(fixedTime)

        var entity : WaterEntity= WaterEntity(
            id = 0L,
            date = fixedTime,
            volume = 1.5f
        )

        WaterRepositoryImpl.insert(
            entity
        )
        advanceUntilIdle()
        secondEmission.await()

        entity.id = 1L //little hack
        WaterRepositoryImpl.delete(entity)
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
            1.5f,
            emissions[1][0].volume
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