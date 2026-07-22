package com.example.test2.exportimport.domain.local

import com.example.test2.TestDateFactory
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.entities.enums.toMask
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.exportimport.data.local.ExportEntity
import com.example.test2.features.exportimport.domain.local.ExportUseCaseImpl
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
import java.time.ZoneOffset

fun assertEndsWith( message: String?, expected: String?, actual:String?) {
    val actualMatch : Boolean = expected?.let { actual?.endsWith(it)?: false } ?: false

    assertEquals( message,  true, actualMatch)
}

fun assertEqualsIgnoreWhiteSpace( message: String?, expected: String?, actual:String?) {
    val expectedMatch : String =  expected?.filterNot { it.isWhitespace() } ?: ""
    val actualMatch : String =  actual?.filterNot { it.isWhitespace() } ?: ""

    assertEquals(
        message,
        expectedMatch,
        actualMatch
    )

}


open class ExportUseCaseTest {
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

        ExportUseCaseImpl.initialize(
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
        insertWeights()
        insertWaters()
        insertNumberTwo()
        insertPillTaken()
        insertActivityTaken()

        val exportedDatabase : String = ExportUseCaseImpl.invokeExport()

        assertNotEquals(
            null,
            exportedDatabase
        )

        assertNotEquals(
            "",
            exportedDatabase
        )

        assertEqualsIgnoreWhiteSpace( "Jsons are differents",takeTheFileFromGradle(), exportedDatabase)

    }

    private fun insertWeights() {

        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )

        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                WeightDAOImpl.insert(
                    WeightEntity(0L, someDayAtTheMorning, iterator.next())
                )
            }
    }

    private fun insertWaters() {

        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                WaterDAOImpl.insert(
                    WaterEntity(0L, someDayAtTheMorning, iterator.next())
                )
            }
    }

    private fun insertNumberTwo() {

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        val days = 50

        dates.take(days)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                NumberTwoDAOImpl.insert(
                    NumberTwoEntity(0L, someDayAtTheMorning, )
                )
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

