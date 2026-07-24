package com.example.test2.exportimport.domain.repository
import com.example.test2.TestDateFactory
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.entities.enums.toMask
import com.example.test2.exportimport.domain.local.assertEndsWith
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.exportimport.domain.local.ExportUseCaseImpl
import com.example.test2.features.exportimport.domain.repository.ExportUseCaseRepositoryImpl
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import java.time.OffsetDateTime


open class ExportUseCaseRepositoryImplTest {
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

        ExportUseCaseImpl.initialize(
            weightDAO = WeightDAOImpl,
            waterDAO=  WaterDAOImpl,
            numberTwoDAO= NumberTwoDAOImpl,
            pillDAO = PillDAOImpl,
            activityDAO = ActivityDAOImpl,
            pillTakenDAO = PillTakenDAOImpl,
            activityTakenDAO = ActivityTakenDAOImpl)

        ExportUseCaseRepositoryImpl.initialize(ExportUseCaseImpl, testDispatcher )
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
    @OptIn(ExperimentalCoroutinesApi::class)
    fun inset_and_validate_data() = testScope.runTest {
        //Simulate the user enter data....
        insertWeights()
        insertWaters()
        insertNumberTwo()
        insertPillTaken()
        insertActivityTaken()

        val exportedDatabase = ExportUseCaseRepositoryImpl.invokeExport()

        assertNotEquals(
            null,
            exportedDatabase
        )

        assertNotEquals(
            "",
            exportedDatabase
        )
        val jsonElement: JsonElement = Json.parseToJsonElement(exportedDatabase)

        jsonElement.jsonObject.let { element: JsonObject ->
            assertTrue(element.containsKey("dailyActivities"))
            assertTrue(element.containsKey("activitiesTaken"))
            assertTrue(element.containsKey("numberTwoEntities"))
            assertTrue(element.containsKey("pillEntities"))
            assertTrue(element.containsKey("pillsTaken"))
            assertTrue(element.containsKey("waters"))
            assertTrue(element.containsKey("weightEntities"))

        }

        assertEquals( "Jsons are differents",takeTheFileFromGradle(), exportedDatabase)
    }


    private fun insertWeights() {

        TestDateFactory.buildWeights(2025, 10, 25, 14, 30, 0, 0,
            36
        ).forEach { we: WeightEntity ->
            WeightDAOImpl.insert(we)
        }

    }

    private fun insertWaters() {

        TestDateFactory.buildWaters(2025, 10, 25, 14, 30, 0, 0,
            36
        ).forEach { we: WaterEntity ->
            WaterDAOImpl.insert(we)
        }
    }

    private fun insertNumberTwo() {

        TestDateFactory.buildNumberTwo(2025, 10, 25, 14, 30, 0, 0,
            50
        ).forEach { nt: NumberTwoEntity ->
            NumberTwoDAOImpl.insert(nt)
        }
    }

    private fun insertPillTaken() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")

        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)

        val pillEntityList : List<PillEntity> = PillDAOImpl.getPills()


        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()
        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                pillEntityList.forEach {
                    val pillTakenEntity = PillTakenEntity.create(pillEntityAsociated = it, date = someDayAtTheMorning, )

                    PillTakenDAOImpl.insert(
                        pillTakenEntity
                    )
                }
            }
    }

    private fun insertActivityTaken() {
        val gym: Set<DaysOfWeekEnum> = setOf(
            DaysOfWeekEnum.MONDAY,
            DaysOfWeekEnum.WEDNESDAY,
            DaysOfWeekEnum.FRIDAY
        )

        val pillDays: Set<DaysOfWeekEnum> = setOf(
            DaysOfWeekEnum.TUESDAY,
            DaysOfWeekEnum.THURSDAY,
            DaysOfWeekEnum.SATURDAY
        )

        val dailyActivities: List<DailyActivityEntity> = listOf(
            DailyActivityEntity(
                0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.WEIGHT_RECORDER.value
            ),
            DailyActivityEntity(
                0L, "Desayunar", 9, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Tomar Agua", 11, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.WATER_RECORDER.value
            ),
            DailyActivityEntity( // "Me tomo una agaromba y todo me chupa un huevo"
                0L, "Recordatorio de tomar la agaromba", 16, 0, daysOfWeek = pillDays.toMask(),
                TypeofRecorder.PILL_RECORDER.value
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
                0L, "Actividad fisica", 18, 0, daysOfWeek = gym.toMask(),
                TypeofRecorder.NONE.value
            ),
        )
        dailyActivities.forEach {
            ActivityDAOImpl.insert(it)
        }


        val activities6OfWeek : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()
        val rating = TestDateFactory.ratingSequence().iterator()

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0
        )
        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()
        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                activities6OfWeek.forEach {
                    val activityTakenEntity : ActivityTakenEntity =
                        ActivityTakenEntity.create(
                            date = someDayAtTheMorning,
                            rating = rating.next(),
                            activityEntityAsociated = it
                        )
                    ActivityTakenDAOImpl.insert(
                        activityTakenEntity
                    )
                }
            }
    }

}

