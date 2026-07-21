package com.example.test2.pill.data.repository

import com.example.test2.features.MyObjectBox
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.pill.data.local.PillDAOImpl
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.pill.data.repository.PillRepositoryImpl
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

open class PillRepositoryTest {

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

        val mPillEntityBox: Box<PillEntity> = store.boxFor(PillEntity::class.java)
        PillDAOImpl.initialize(mPillEntityBox)
        PillRepositoryImpl.initialize(PillDAOImpl, testDispatcher)
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

    val emissions = mutableListOf<List<PillEntity>>()
    val secondEmission = CompletableDeferred<Unit>()
    val thirdEmission = CompletableDeferred<Unit>()

    val collectorJob = testScope.launch {

        PillRepositoryImpl
            .getPills()
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

    val expectedName = "Vitaminas"
    PillRepositoryImpl.insert(
        PillEntity(
            0L, expectedName
        )
    )
    advanceUntilIdle()
    secondEmission.await()
    PillRepositoryImpl.deleteAll()
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
        expectedName,
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

        val emissions = mutableListOf<List<PillEntity>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {

            PillRepositoryImpl
                .getPills()
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
        val expectedName = "Vitaminas"
        val entity : PillEntity= PillEntity(
            0L, expectedName
        )

        PillRepositoryImpl.insert(
            entity
        )
        advanceUntilIdle()
        secondEmission.await()
        entity.id = 1L //little hack
        PillRepositoryImpl.delete(entity)
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
            expectedName,
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