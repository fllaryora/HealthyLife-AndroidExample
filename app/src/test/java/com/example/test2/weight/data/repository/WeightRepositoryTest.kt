package com.example.test2.weight.data.repository

import com.example.test2.MyObjectBox
import com.example.test2.features.weight.data.local.WeightDAOImpl
import com.example.test2.features.weight.data.local.WeightEntity
import com.example.test2.features.weight.data.repository.WeightRepositoryImpl
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

open class WeightRepositoryTest {

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

        val mWeightEntityBox: Box<WeightEntity> = store.boxFor(WeightEntity::class.java)
        WeightDAOImpl.initialize(mWeightEntityBox)
        WeightRepositoryImpl.initialize(WeightDAOImpl, testDispatcher)
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

    val emissions = mutableListOf<List<WeightEntity>>()
    val secondEmission = CompletableDeferred<Unit>()
    val thirdEmission = CompletableDeferred<Unit>()

    val collectorJob = testScope.launch {

        WeightRepositoryImpl
            .getAll()
            .collect { list ->


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

    WeightRepositoryImpl.insert(
        WeightEntity(
            id = 0L,
            date = OffsetDateTime.now(),
            weight = 1.5f
        )
    )
    advanceUntilIdle()
    secondEmission.await()
    WeightRepositoryImpl.deleteAll()
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
        emissions[1][0].weight
    )

    assertEquals(
        0,
        emissions[2].size
    )
}

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insert_and_delete_should_emit_changes_after_insert_and_emit_after_delete() = testScope.runTest {

        val emissions = mutableListOf<List<WeightEntity>>()
        val secondEmission = CompletableDeferred<Unit>()
        val thirdEmission = CompletableDeferred<Unit>()

        val collectorJob = testScope.launch {

            WeightRepositoryImpl
                .getAll()
                .collect { list ->


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
        val entity : WeightEntity= WeightEntity(
            id = 0L,
            date = OffsetDateTime.now(),
            weight = 1.5f
        )

        WeightRepositoryImpl.insert(
            entity
        )
        advanceUntilIdle()
        secondEmission.await()
        entity.id = 1L //little hack
        WeightRepositoryImpl.delete(entity)
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
            emissions[1][0].weight
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