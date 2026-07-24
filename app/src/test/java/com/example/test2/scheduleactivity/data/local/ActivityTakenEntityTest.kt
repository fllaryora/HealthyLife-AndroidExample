package com.example.test2.scheduleactivity.data.local

import com.example.test2.TestDateFactory
import com.example.test2.data.entities.behaviors.groupByOwnerId
import com.example.test2.data.entities.behaviors.importAndGetComparableIDsMap
import com.example.test2.data.entities.behaviors.importTakenEntitiesResolvingOwners
import com.example.test2.data.entities.behaviors.prepareForImport
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.entities.enums.toMask
import com.example.test2.exportimport.domain.local.assertEndsWith
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.exportimport.domain.local.jsonPropertiesForExport
import com.example.test2.features.recordactivity.data.local.ActivityTakenDAOImpl
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.net.URL
import java.time.OffsetDateTime
import kotlin.collections.get
import kotlin.text.set

open class ActivityTakenEntityTest {

    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!


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

        val mActivityBox: Box<DailyActivityEntity> = store.boxFor(DailyActivityEntity::class.java)
        val mActivityTakenEntityBox: Box<ActivityTakenEntity> = store.boxFor(ActivityTakenEntity::class.java)
        ActivityDAOImpl.initialize(mActivityBox)
        ActivityTakenDAOImpl.initialize(mActivityTakenEntityBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {

        val allList : List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()
        Assert.assertEquals(0, allList.size)

        val activity = DailyActivityEntity(
            0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.MONDAY.value,
            TypeofRecorder.NONE.value
        )
        ActivityDAOImpl.insert(activity)
        val actList : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()

        var actT1 = ActivityTakenDAOImpl.getActivityTaken(actList.first(), 0, 20L)
        Assert.assertEquals(0, actT1.first.size)

        var actT2 = ActivityTakenDAOImpl.getActivityTakenList(actList.first(), 0, 20L)
        Assert.assertEquals(0, actT2.size)

        var actT3 = ActivityTakenDAOImpl.getAllByActivity(actList.first())
        Assert.assertEquals(0, actT3.size)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        //Its not in order to check the order.
        val dailyActivities: List<DailyActivityEntity> = listOf(
            DailyActivityEntity(
                0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.MONDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Desayunar", 9, 0, daysOfWeek = DaysOfWeekEnum.FRIDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Cenar", 21, 0, daysOfWeek = DaysOfWeekEnum.SUNDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Almorzar", 13, 0, daysOfWeek = DaysOfWeekEnum.TUESDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Merendar", 17, 0, daysOfWeek = DaysOfWeekEnum.THURSDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Actividad fisica", 18, 0, daysOfWeek = DaysOfWeekEnum.WEDNESDAY.value,
                TypeofRecorder.NONE.value
            ),
        )
        dailyActivities.forEach {
            ActivityDAOImpl.insert(it)
        }


        val activitiesOfWeek : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        val rating = TestDateFactory.ratingSequence().iterator()
        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                activitiesOfWeek.forEach {
                    val activityTakenEntity : ActivityTakenEntity =
                        ActivityTakenEntity.create(
                            date=  someDayAtTheMorning,
                            rating = rating.next(),
                            activityEntityAsociated = it
                        )
                    ActivityTakenDAOImpl.insert(
                        activityTakenEntity
                    )
                }
            }

        //6 activities  in 36 days
        val allList2 : List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()
        Assert.assertEquals(36 * 6, allList2.size)

        val actList : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()

        var actT1 = ActivityTakenDAOImpl.getActivityTaken(actList.first(), 0, 20L)
        Assert.assertEquals(20, actT1.first.size)

        var actT2 = ActivityTakenDAOImpl.getActivityTakenList(actList.first(), 0, 20L)
        Assert.assertEquals(20, actT2.size)

        //1 activity  in 36 days
        var actT3 = ActivityTakenDAOImpl.getAllByActivity(actList.first())
        Assert.assertEquals(36, actT3.size)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        ActivityTakenDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {
        //Its not in order to check the order.
        val dailyActivities: List<DailyActivityEntity> = listOf(
            DailyActivityEntity(
                0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.MONDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Desayunar", 9, 0, daysOfWeek = DaysOfWeekEnum.FRIDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Cenar", 21, 0, daysOfWeek = DaysOfWeekEnum.SUNDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Almorzar", 13, 0, daysOfWeek = DaysOfWeekEnum.TUESDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Merendar", 17, 0, daysOfWeek = DaysOfWeekEnum.THURSDAY.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Actividad fisica", 18, 0, daysOfWeek = DaysOfWeekEnum.WEDNESDAY.value,
                TypeofRecorder.NONE.value
            ),
        )
        dailyActivities.forEach {
            ActivityDAOImpl.insert(it)
        }


        val activities6OfWeek : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        val rating = TestDateFactory.ratingSequence().iterator()

        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                activities6OfWeek.forEach {
                    val activityTakenEntity : ActivityTakenEntity =
                        ActivityTakenEntity.create(
                            date=  someDayAtTheMorning,
                            rating = rating.next(),
                            activityEntityAsociated = it
                        )
                    ActivityTakenDAOImpl.insert(
                        activityTakenEntity
                    )
                }
            }

        // 6 activities , taken in 36 days
        var allActivitiesTaken : List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()
        Assert.assertEquals(36 * 6, allActivitiesTaken.size)
        ActivityTakenDAOImpl.delete(allActivitiesTaken.first())
        allActivitiesTaken  = ActivityTakenDAOImpl.getAll()
        // 6 activities , taken in 36 days , except one
        Assert.assertEquals((36 * 6) -1, allActivitiesTaken.size)

        ActivityTakenDAOImpl.deleteByActivity(allActivitiesTaken.first().activity.target)
        allActivitiesTaken  = ActivityTakenDAOImpl.getAll()
        // 5 activities , taken in 36 days , except one
        Assert.assertEquals((36 * 5) -1, allActivitiesTaken.size)
    }


    @Test
    fun encodeTest() {
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

        val names: List<String> = listOf(
            "Pesarse",
            "Desayunar",
            "Tomar Agua",
            "Recordatorio de tomar la agaromba",
            "Cenar",
            "Almorzar",
            "Merendar",
            "Actividad fisica",
        )

        assertEquals(8, names.size)

        val dailyActivities: List<DailyActivityEntity> = listOf(
            DailyActivityEntity(
                0L, names[0], 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.WEIGHT_RECORDER.value
            ),
            DailyActivityEntity(
                0L, names[1], 9, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, names[2], 11, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.WATER_RECORDER.value
            ),
            DailyActivityEntity( // "Me tomo una agaromba y todo me chupa un huevo"
                0L, names[3], 16, 0, daysOfWeek = pillDays.toMask(),
                TypeofRecorder.PILL_RECORDER.value
            ),
            DailyActivityEntity(
                0L, names[4], 21, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, names[5], 13, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, names[6], 17, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, names[7], 18, 0, daysOfWeek = gym.toMask(),
                TypeofRecorder.NONE.value
            ),
        )
        dailyActivities.forEach {
            ActivityDAOImpl.insert(it)
        }

        val activities6OfWeek : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        val rating = TestDateFactory.ratingSequence().iterator()

        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                activities6OfWeek.forEach {
                    val activityTakenEntity : ActivityTakenEntity =
                        ActivityTakenEntity.create(
                            date=  someDayAtTheMorning,
                            rating = rating.next(),
                            activityEntityAsociated = it
                        )
                    ActivityTakenDAOImpl.insert(
                        activityTakenEntity
                    )
                }
            }

        // 6 activities , taken in 36 days
        var allActivitiesTaken : List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()

        assertEquals(36 * 8, allActivitiesTaken.size)

        val prettyJson:String = jsonPropertiesForExport.encodeToString(allActivitiesTaken)

        //print(prettyJson)

        val jsonElement = Json.parseToJsonElement(prettyJson)

        jsonElement.jsonArray.forEach { element: JsonElement ->
            val currentObj: JsonObject = element.jsonObject
            assertTrue(currentObj.containsKey("id"))
            assertTrue(currentObj.containsKey("date"))
            assertTrue(currentObj.containsKey("rating"))
            assertTrue(currentObj.containsKey("isTaken"))
            assertTrue(currentObj.containsKey("exportActivityId"))

            val fk: Long? = currentObj["exportActivityId"]?.jsonPrimitive?.long

            fk?.let {
                assertNotEquals(0L, it)
            }

        }

    }

    private fun takeTheFileFromGradle(file: String = "activities.json") : String {
        val expectedDataBaseFile: URL = javaClass.classLoader!!
            .getResource(file)

        /*This part is a crapy part
        because it will fail outside gradle world
        * */
        println(expectedDataBaseFile.file)
        assertEndsWith(
            "The path of the resource file",
            "app/build/intermediates/java_res/debugUnitTest/processDebugUnitTestJavaRes/out/${file}",
            expectedDataBaseFile.file
        )


        val databaseString = javaClass.classLoader!!
            .getResource(file)!!
            .readText()

        return databaseString
    }

    /**
     * Validates the complete import workflow:
     *
     * 1. Deserialize DailyActivityEntity list from exported JSON.
     * 2. Deserialize ActivityTakenEntity list from exported JSON.
     * 3. Import DailyActivityEntity instances in a stable order.
     * 4. Preserve a mapping between exported IDs and newly generated IDs.
     * 5. Verify that all DailyActivityEntity records were imported.
     * 6. Group ActivityTakenEntity records by exportActivityId.
     * 7. Resolve each exported activity reference to the newly imported activity.
     * 8. Import ActivityTakenEntity records while rebuilding ObjectBox relations.
     * 9. Verify that all ActivityTakenEntity records were imported.
     * 10. Verify that every imported ActivityTakenEntity has a valid activity relation.
     */
    @Test
    fun decodeTest() {
        // Arrange
        // list of activities deserialized
        val importEntity :List<DailyActivityEntity> = Json.decodeFromString<List<DailyActivityEntity>>(takeTheFileFromGradle())
        // list of activities taken deserialized
        val importTakenEntity :List<ActivityTakenEntity> = Json.decodeFromString<List<ActivityTakenEntity>>(takeTheFileFromGradle("activityTaken.json"))

        val importedActivitiesByOldId: Map<Long, DailyActivityEntity > =
        importEntity.importAndGetComparableIDsMap( insert = { activityToInsert: DailyActivityEntity ->
            // Act
            ActivityDAOImpl.insert(activityToInsert)
        } )

        // Arrange
        // 2. Group All ActivityTakenEntity in the list by exportActivityId
        val takenGroupedByActivity : Map <Long, List<ActivityTakenEntity>> =
            importTakenEntity.groupByOwnerId()

        // 3. Insert ActivityTakenEntity using  DailyActivityEntity
        // DailyActivityEntity has already persisted
        takenGroupedByActivity.importTakenEntitiesResolvingOwners(
            importedOwnersByOldId = importedActivitiesByOldId,
            insert = { prepared: ActivityTakenEntity ->
                ActivityTakenDAOImpl.insert(prepared)
        })

        // Assert
        val list: List<DailyActivityEntity> = ActivityDAOImpl.getActivities()
        Assert.assertEquals(importEntity.size, list.size)

        val listTaken: List<ActivityTakenEntity> = ActivityTakenDAOImpl.getAll()
        Assert.assertEquals(importTakenEntity.size, listTaken.size)
        listTaken.forEach {
            Assert.assertTrue(
                "Activity relation was not restored",
                it.activity.targetId > 0L
            )
        }
    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}