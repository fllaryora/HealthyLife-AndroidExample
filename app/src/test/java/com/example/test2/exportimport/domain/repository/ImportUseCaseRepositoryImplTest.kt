package com.example.test2.exportimport.domain.repository
import com.example.test2.exportimport.domain.local.assertEndsWith
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.exportimport.data.local.ExportEntity
import com.example.test2.features.exportimport.domain.local.ImportUseCaseImpl
import com.example.test2.features.numbertwo.data.local.NumberTwoDAOImpl
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.pill.data.local.PillDAOImpl
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAOImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.recordpill.data.local.PillTakenDAOImpl
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.water.data.local.WaterDAOImpl
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.weight.data.local.WeightDAOImpl
import com.example.test2.features.weight.data.local.WeightEntity
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.net.URL
import com.example.test2.features.exportimport.domain.repository.ImportUseCaseRepositoryImpl
import io.objectbox.kotlin.flow
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest


open class ImportUseCaseRepositoryImplTest {
    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }

    val testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)

    lateinit var mWeightEntityBox: Box<WeightEntity>
    lateinit var mWaterBox: Box<WaterEntity>
    lateinit var mNumberTwoEntityBox: Box<NumberTwoEntity>
    lateinit var mPillEntityBox: Box<PillEntity>
    lateinit var mActivityBox : Box<DailyActivityEntity>
    lateinit var mPillTakenEntityBox: Box<PillTakenEntity>
    lateinit var mActivityTakenEntityBox: Box<ActivityTakenEntity>

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


        mWeightEntityBox = store.boxFor(WeightEntity::class.java)
        mWaterBox = store.boxFor(WaterEntity::class.java)
        mNumberTwoEntityBox = store.boxFor(NumberTwoEntity::class.java)
        mPillEntityBox = store.boxFor(PillEntity::class.java)
        mActivityBox = store.boxFor(DailyActivityEntity::class.java)
        mPillTakenEntityBox = store.boxFor(PillTakenEntity::class.java)
        mActivityTakenEntityBox  = store.boxFor(ActivityTakenEntity::class.java)

        WeightDAOImpl.initialize(mWeightEntityBox)
        WaterDAOImpl.initialize(mWaterBox)
        NumberTwoDAOImpl.initialize(mNumberTwoEntityBox)
        PillDAOImpl.initialize(mPillEntityBox)
        ActivityDAOImpl.initialize(mActivityBox)
        PillTakenDAOImpl.initialize(mPillTakenEntityBox)
        ActivityTakenDAOImpl.initialize(mActivityTakenEntityBox)

        ImportUseCaseImpl.initialize(
            weightDAO = WeightDAOImpl,
            waterDAO=  WaterDAOImpl,
            numberTwoDAO= NumberTwoDAOImpl,
            pillDAO = PillDAOImpl,
            activityDAO = ActivityDAOImpl,
            pillTakenDAO = PillTakenDAOImpl,
            activityTakenDAO = ActivityTakenDAOImpl)

        ImportUseCaseRepositoryImpl.initialize(ImportUseCaseImpl, testDispatcher )
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }


    private fun takeTheFileFromGradle() : String {
        val expectedDataBaseFile: URL = javaClass.classLoader!!
            .getResource("expectedDataBase.json")

        /*This part is a crapy part
        because it will fail outside gradle world
        * */
        assertEndsWith(
            "The path of the resource file",
            "app/build/intermediates/java_res/debugUnitTest/processDebugUnitTestJavaRes/out/expectedDataBase.json",
            expectedDataBaseFile.file
        )

        val databaseString = javaClass.classLoader!!
            .getResource("expectedDataBase.json")!!
            .readText()

        return databaseString
    }

    @Test
    fun testResource() {

        val importEntity :ExportEntity = Json.decodeFromString<ExportEntity>(takeTheFileFromGradle())

        assertTrue("the resource is corrupt", importEntity.pillEntities.isNotEmpty())
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun inset_and_validate_data() = testScope.runTest {
        val weightEmissions = mutableListOf<List<WeightEntity>>()
        val waterEmissions = mutableListOf<List<WaterEntity>>()
        val numberTwoEmissions = mutableListOf<List<NumberTwoEntity>>()
        val pillEmissions = mutableListOf<List<PillEntity>>()
        val activityEmissions = mutableListOf<List<DailyActivityEntity>>()
        val pillTakenEmissions = mutableListOf<List<PillTakenEntity>>()
        val activityTakenEmissions = mutableListOf<List<ActivityTakenEntity>>()

        val expectedWeightCount: Int = 36
        val expectedWaterCount: Int = 36
        val expectedNumberTwoCount: Int = 50
        val expectedPillCount: Int = 2
        val expectedActivityCount: Int = 8
        val expectedPillTakenCount: Int = expectedPillCount * 36
        val expectedActivityTakenCount: Int = expectedActivityCount * 36

        val weightCollectorJob: Job =
            testScope.launch {
                mWeightEntityBox.query().build().flow()
                    .map { it.toList<WeightEntity>() }
                    .flowOn(testDispatcher).collect {
                        list: List<WeightEntity> ->

                    weightEmissions.add(list)

                    if (list.size == expectedWeightCount) {
                        cancel()
                    }
                }
            }
        advanceUntilIdle()
        val waterCollectorJob: Job =
            testScope.launch {
                mWaterBox.query().build().flow()
                    .map { it.toList<WaterEntity>() }
                    .flowOn(testDispatcher).collect {
                        list: List<WaterEntity> ->

                    waterEmissions.add(list)

                    if (list.size == expectedWaterCount) {
                        cancel()
                    }
                }
            }
        advanceUntilIdle()
        val numberTwoCollectorJob: Job =
            testScope.launch {
                mNumberTwoEntityBox.query().build().flow()
                    .map { it.toList<NumberTwoEntity>() }
                    .flowOn(testDispatcher).collect {
                        list: List<NumberTwoEntity> ->

                    numberTwoEmissions.add(list)

                    if (list.size == expectedNumberTwoCount) {
                        cancel()
                    }
                }
            }

        advanceUntilIdle()
        val pillCollectorJob: Job =
            testScope.launch {
                mPillEntityBox.query().build().flow()
                    .map { it.toList<PillEntity>() }
                    .flowOn(testDispatcher).collect {
                        list: List<PillEntity> ->

                    pillEmissions.add(list)

                    if (list.size == expectedPillCount) {
                        cancel()
                    }
                }
            }
        advanceUntilIdle()
        val activityCollectorJob: Job =
            testScope.launch {
                mActivityBox.query().build().flow()
                    .map { it.toList<DailyActivityEntity>() }
                    .flowOn(testDispatcher).collect {
                        list: List<DailyActivityEntity> ->

                    activityEmissions.add(list)

                    if (list.size == expectedActivityCount) {
                        cancel()
                    }
                }
            }

        advanceUntilIdle()
        val pillTakenCollectorJob: Job =
            testScope.launch {
                mPillTakenEntityBox.query().build().flow()
                    .map { it.toList<PillTakenEntity>() }
                    .flowOn(testDispatcher).collect {
                        list: List<PillTakenEntity> ->

                    pillTakenEmissions.add(list)

                    if (list.size == expectedPillTakenCount) {
                        cancel()
                    }
                }
            }

        advanceUntilIdle()
        val activityTakenCollectorJob: Job =
            testScope.launch {
                mActivityTakenEntityBox.query().build().flow()
                    .map { it.toList<ActivityTakenEntity>() }
                    .flowOn(testDispatcher).collect {
                        list: List<ActivityTakenEntity> ->

                    activityTakenEmissions.add(list)

                    if (list.size == expectedActivityTakenCount) {
                        cancel()
                    }
                }
            }

        advanceUntilIdle()

        ImportUseCaseRepositoryImpl.invokeImport(takeTheFileFromGradle())
        weightCollectorJob.join()
        waterCollectorJob.join()
        numberTwoCollectorJob.join()
        pillCollectorJob.join()
        activityCollectorJob.join()
        pillTakenCollectorJob.join()
        activityTakenCollectorJob.join()

        //first emission is empty
        assertTrue(weightEmissions[0].isEmpty())
        assertTrue(waterEmissions[0].isEmpty())
        assertTrue(numberTwoEmissions[0].isEmpty())
        assertTrue(pillEmissions[0].isEmpty())
        assertTrue(activityEmissions[0].isEmpty())
        assertTrue(pillTakenEmissions[0].isEmpty())
        assertTrue(activityTakenEmissions[0].isEmpty())

        assertEquals(expectedWeightCount, weightEmissions.last().size)
        assertEquals(expectedWaterCount, waterEmissions.last().size)
        assertEquals(expectedNumberTwoCount, numberTwoEmissions.last().size)
        assertEquals(expectedPillCount, pillEmissions.last().size)
        assertEquals(expectedActivityCount, activityEmissions.last().size)
        assertEquals(expectedPillTakenCount, pillTakenEmissions.last().size)
        assertEquals(expectedActivityTakenCount, activityTakenEmissions.last().size)

    }



}

