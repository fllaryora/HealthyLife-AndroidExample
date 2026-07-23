package com.example.test2.dailyactivity.data.local

import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.entities.enums.toMask
import com.example.test2.features.MyObjectBox
import com.example.test2.features.dailyactivity.data.local.ActivityDAOImpl
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.exportimport.domain.local.jsonPropertiesForExport
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import io.objectbox.config.DebugFlags
import junit.framework.TestCase.assertEquals
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.random.Random

open class DailyActivityEntityTest {

    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!

    private fun coin(from: Int, until: Int): Int {
        return Random(System.nanoTime()).nextInt(from = from, until = until)
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

        val mPillBox: Box<DailyActivityEntity> = store.boxFor(DailyActivityEntity::class.java)
        ActivityDAOImpl.initialize(mPillBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        val allList : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()
        Assert.assertEquals(0, allList.size)
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

        val allList : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()
        Assert.assertEquals(6, allList.size)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        ActivityDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {
        //Its not in order to check the order.
        val daylyActivities: List<DailyActivityEntity> = listOf(
            DailyActivityEntity(
                0L, "Pesarse", 8, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
            DailyActivityEntity(
                0L, "Desayunar", 9, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
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
                0L, "Actividad fisica", 18, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
                TypeofRecorder.NONE.value
            ),
        )

        daylyActivities.forEach {
            ActivityDAOImpl.insert(it)
        }

        var allList = ActivityDAOImpl.getActivities()
        val touch : Int  = coin(0, allList.size-1)
        allList.forEachIndexed { index, weight : DailyActivityEntity ->
            if( index != touch){
                ActivityDAOImpl.delete(weight)
            }
        }

        allList  = ActivityDAOImpl.getActivities()
        Assert.assertEquals(1, allList.size)

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

        val allList : List<DailyActivityEntity> = ActivityDAOImpl.getActivities()

        val prettyJson:String = jsonPropertiesForExport.encodeToString(allList)

        print(prettyJson)

        val jsonElement = Json.parseToJsonElement(prettyJson)

        jsonElement.jsonArray.forEach { element: JsonElement ->
            val currentObj: JsonObject = element.jsonObject
            assertTrue(currentObj.containsKey("id"))
            assertTrue(currentObj.containsKey("name"))
            assertTrue(currentObj.containsKey("hour"))
            assertTrue(currentObj.containsKey("minute"))
            assertTrue(currentObj.containsKey("daysOfWeek"))
            assertTrue(currentObj.containsKey("typeOfRecorder"))
            assertTrue(currentObj.containsKey("isAlarmEnabled"))
            val currentName: String? = currentObj["name"]?.jsonPrimitive?.content
            val currentId: Long? = currentObj["id"]?.jsonPrimitive?.long
            currentId?.let {
                assertEquals(names[it.toInt()-1], currentName)
            }

        }

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}