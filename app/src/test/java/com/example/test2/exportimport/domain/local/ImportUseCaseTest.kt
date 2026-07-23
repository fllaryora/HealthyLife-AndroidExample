package com.example.test2.exportimport.domain.local

import com.example.test2.TestDateFactory
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.entities.enums.toMask
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.net.URL
import java.time.OffsetDateTime


open class ImportUseCaseTest {
    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
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

        val mWeightEntityBox: Box<WeightEntity> = store.boxFor(WeightEntity::class.java)
        WeightDAOImpl.initialize(mWeightEntityBox)

        val mWaterBox: Box<WaterEntity> = store.boxFor(WaterEntity::class.java)
        WaterDAOImpl.initialize(mWaterBox)

        val mNumberTwoEntityBox: Box<NumberTwoEntity> = store.boxFor(NumberTwoEntity::class.java)
        NumberTwoDAOImpl.initialize(mNumberTwoEntityBox)

        val mPillEntityBox: Box<PillEntity> = store.boxFor(PillEntity::class.java)
        PillDAOImpl.initialize(mPillEntityBox)

        val mActivityBox : Box<DailyActivityEntity> = store.boxFor(DailyActivityEntity::class.java)
        ActivityDAOImpl.initialize(mActivityBox)

        val mPillTakenEntityBox: Box<PillTakenEntity> = store.boxFor(PillTakenEntity::class.java)
        PillTakenDAOImpl.initialize(mPillTakenEntityBox)

        val mActivityTakenEntityBox: Box<ActivityTakenEntity>  = store.boxFor(ActivityTakenEntity::class.java)
        ActivityTakenDAOImpl.initialize(mActivityTakenEntityBox)

        ImportUseCaseImpl.initialize(
            weightDAO = WeightDAOImpl,
            waterDAO=  WaterDAOImpl,
            numberTwoDAO= NumberTwoDAOImpl,
            pillDAO = PillDAOImpl,
            activityDAO = ActivityDAOImpl,
            pillTakenDAO = PillTakenDAOImpl,
            activityTakenDAO = ActivityTakenDAOImpl)
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
    fun inset_and_validate_data() {

        //Simulate the user enter data....


        ImportUseCaseImpl.invokeImport(takeTheFileFromGradle())

        checkWeights()
        checkWaters()
        checkNumberTwo()

        checkPillTaken()

    }

    private fun checkWeights() {

        val listToMatch: List<WeightEntity> = TestDateFactory.buildWeights(2025, 10, 25, 14, 30, 0, 0,
            36
        ).mapIndexed { index, entity ->
            entity.id = (index+1).toLong()
            return@mapIndexed entity
        }
        val actualList: List<WeightEntity> = WeightDAOImpl.getAll()

        assertEquals(listToMatch, actualList )

    }

    private fun checkWaters() {

        val listToMatch: List<WaterEntity> =  TestDateFactory.buildWaters(2025, 10, 25, 14, 30, 0, 0,
            36
        ).mapIndexed { index, entity ->
            entity.id = (index+1).toLong()
            return@mapIndexed entity
        }
        val actualList: List<WaterEntity> = WaterDAOImpl.getAll()

        assertEquals(listToMatch, actualList )

    }

    private fun checkNumberTwo() {

        val listToMatch: List<NumberTwoEntity> =
            TestDateFactory.buildNumberTwo(
                2025, 10, 25, 14, 30, 0, 0,
                50
            ).mapIndexed { index, entity ->
                entity.id = (index + 1).toLong()
                entity
            }

        val actualList: List<NumberTwoEntity> =
            NumberTwoDAOImpl.getAll()

        assertEquals(listToMatch, actualList)
    }

    private fun checkPillTaken() {
        val actualPillList : List<PillEntity> = PillDAOImpl.getPills()
        val expected: List<PillEntity> = listOf(
            PillEntity(1L, "Supradin Forte"),
            PillEntity(2L, "Total Magneciano")
        )
        assertEquals(expected, actualPillList)

        val expectedTaken: MutableList<PillTakenEntity> = mutableListOf<PillTakenEntity>()

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()
        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                expected.forEachIndexed { index, entity ->
                    val pillTakenEntity = PillTakenEntity.create(pillEntityAsociated = entity, date = someDayAtTheMorning, )
                    pillTakenEntity.id =  (index+1).toLong()
                    expectedTaken.add(pillTakenEntity)
                }
            }

        val actualTaken : List<PillTakenEntity> = PillTakenDAOImpl.getAll()

        assertEquals(expectedTaken, actualTaken)
    }

}

