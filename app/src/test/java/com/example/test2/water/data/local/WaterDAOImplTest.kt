package com.example.test2.water.data.local

import com.example.test2.TestDateFactory
import com.example.test2.data.entities.behaviors.prepareForImport
import com.example.test2.exportimport.domain.local.assertEndsWith
import com.example.test2.features.MyObjectBox
import com.example.test2.features.exportimport.domain.local.jsonPropertiesForExport
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.water.data.local.WaterDAOImpl
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.collections.forEachIndexed
import kotlin.random.Random

open class WaterDAOImplTest {

    private var _store: BoxStore? = null
    protected val store: BoxStore
        get() = _store!!

    private fun coin(from: Int, until: Int): Int {
        return Random(System.nanoTime()).nextInt(from = from, until = until)
    }

    private fun rand(): Float {
        return Random(System.nanoTime()).nextDouble(70.0, 180.0).toFloat()
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

        val mWaterBox: Box<WaterEntity> = store.boxFor(WaterEntity::class.java)
        WaterDAOImpl.initialize(mWaterBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        // Get a box and use ObjectBox as usual
        val fixedTime: OffsetDateTime = OffsetDateTime.of(
            2025, 10, 25, 14, 30, 0, 0,
            ZoneOffset.ofHours(-3) // Example: UTC-3 for Argentina
        )
        val list: List<WaterEntity> = WaterDAOImpl.getWaters(0L, 20L)
        val allList : List<WaterEntity> = WaterDAOImpl.getAll()
        val dayList : List<WaterEntity> = WaterDAOImpl.getIntakesByDay(fixedTime)
        Assert.assertEquals(0, list.size)
        Assert.assertEquals(0, allList.size)
        Assert.assertEquals(0, dayList.size)
    }

    @Test
    fun insetWeightsAndGetTheData() {

        val dates : Sequence<OffsetDateTime> =
            TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        val iterator : Iterator<Float> = TestDateFactory.weightSequence().iterator()
        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                WaterDAOImpl.insert(
                    WaterEntity(0L, someDayAtTheMorning, iterator.next())
                )
            }


        val list: List<WaterEntity> = WaterDAOImpl.getWaters(0L, 20L)
        val allList : List<WaterEntity> = WaterDAOImpl.getAll()
        Assert.assertEquals(20, list.size)
        Assert.assertEquals(36, allList.size)

        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                 val dayList : List<WaterEntity>  = WaterDAOImpl.getIntakesByDay(someDayAtTheMorning)
                Assert.assertEquals(1, dayList.size)
            }

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        WaterDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {

        TestDateFactory.buildWaters(2025, 10, 25, 14, 30, 0, 0,
            36
        ).forEach { we: WaterEntity ->
                WaterDAOImpl.insert(we)
            }

        var allList = WaterDAOImpl.getAll()
        val touch : Int  = coin(0, allList.size-1)
        allList.forEachIndexed { index, water : WaterEntity ->
            if( index != touch){
                WaterDAOImpl.delete(water)
            }
        }

        allList = WaterDAOImpl.getAll()

        Assert.assertEquals(1, allList.size)

    }

    @Test
    fun encodeTest() {
        TestDateFactory.buildWaters(2025, 10, 25, 14, 30, 0, 0,
            36
        ).forEach { we: WaterEntity ->
            WaterDAOImpl.insert(we)
        }

        val allList : List<WaterEntity> = WaterDAOImpl.getAll()


        val prettyJson:String = jsonPropertiesForExport.encodeToString(allList)

        val jsonElement: JsonElement = Json.parseToJsonElement(prettyJson)

        jsonElement.jsonArray.forEach { element: JsonElement ->
            val obj: JsonObject = element.jsonObject
            assertTrue(obj.containsKey("id"))
            assertTrue(obj.containsKey("date"))
            assertTrue(obj.containsKey("volume"))
        }
    }

    private fun takeTheFileFromGradle() : String {
        val expectedDataBaseFile: URL = javaClass.classLoader!!
            .getResource("waters.json")

        /*This part is a crapy part
        because it will fail outside gradle world
        * */
        println(expectedDataBaseFile.file)
        assertEndsWith(
            "The path of the resource file",
            "app/build/intermediates/java_res/debugUnitTest/processDebugUnitTestJavaRes/out/waters.json",
            expectedDataBaseFile.file
        )


        val databaseString = javaClass.classLoader!!
            .getResource("waters.json")!!
            .readText()

        return databaseString
    }

    @Test
    fun decodeTest() {

        val importEntity :List<WaterEntity> = Json.decodeFromString<List<WaterEntity>>(takeTheFileFromGradle())

        importEntity.prepareForImport().forEach { we: WaterEntity ->
            WaterDAOImpl.insert(we)
        }

        val list: List<WaterEntity> = WaterDAOImpl.getAll()
        assertEquals(36, list.size)

    }
    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}