package com.example.test2.numbertwo.data.local

import com.example.test2.TestDateFactory
import com.example.test2.data.entities.behaviors.prepareForImport
import com.example.test2.exportimport.domain.local.assertEndsWith
import com.example.test2.features.MyObjectBox
import com.example.test2.features.exportimport.domain.local.jsonPropertiesForExport
import com.example.test2.features.numbertwo.data.local.NumberTwoDAOImpl
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.water.data.local.WaterDAOImpl
import com.example.test2.features.water.data.local.WaterEntity
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
import kotlin.random.Random

open class NumberTwoEntityTest {

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

        val mNumberTwoEntityBox: Box<NumberTwoEntity> = store.boxFor(NumberTwoEntity::class.java)
        NumberTwoDAOImpl.initialize(mNumberTwoEntityBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        val list: List<NumberTwoEntity> = NumberTwoDAOImpl.getNumberTwoList(0L, 20L)
        val allList : List<NumberTwoEntity> = NumberTwoDAOImpl.getAll()

        Assert.assertEquals(0, list.size)
        Assert.assertEquals(0, allList.size)

    }

    @Test
    fun insetBathroomVisitsAndGetTheData() {
        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0
        )
        val days = 50
        dates.take(days)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                NumberTwoDAOImpl.insert(
                    NumberTwoEntity(0L, someDayAtTheMorning, )
                )
            }
        val list: List<NumberTwoEntity> = NumberTwoDAOImpl.getNumberTwoList(0L, 20L)
        val allList : List<NumberTwoEntity> = NumberTwoDAOImpl.getAll()
        Assert.assertEquals(20, list.size)
        Assert.assertEquals(days, allList.size)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        NumberTwoDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {
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
        var allList = NumberTwoDAOImpl.getAll()
        val touch : Int  = coin(0, allList.size-1)
        allList.forEachIndexed { index, water : NumberTwoEntity ->
            if( index != touch){
                NumberTwoDAOImpl.delete(water)
            }
        }

        val list: List<NumberTwoEntity> = NumberTwoDAOImpl.getNumberTwoList(0L, 20L)
        allList = NumberTwoDAOImpl.getAll()
        Assert.assertEquals(1, list.size)
        Assert.assertEquals(1, allList.size)

    }

    @Test
    fun encodeTest() {

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0
        )
        val days = 50
        dates.take(days)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                NumberTwoDAOImpl.insert(
                    NumberTwoEntity(0L, someDayAtTheMorning, )
                )
            }
        var allList: List<NumberTwoEntity> = NumberTwoDAOImpl.getAll()

        val prettyJson:String = jsonPropertiesForExport.encodeToString(allList)

        val jsonElement: JsonElement = Json.parseToJsonElement(prettyJson)

        jsonElement.jsonArray.forEach { element: JsonElement ->
            val obj: JsonObject = element.jsonObject
            assertTrue(obj.containsKey("id"))
            assertTrue(obj.containsKey("date"))
            assertTrue(obj.containsKey("isTaken"))
        }
    }

    private fun takeTheFileFromGradle() : String {
        val expectedDataBaseFile: URL = javaClass.classLoader!!
            .getResource("numbertwo.json")

        /*This part is a crapy part
        because it will fail outside gradle world
        * */
        println(expectedDataBaseFile.file)
        assertEndsWith(
            "The path of the resource file",
            "app/build/intermediates/java_res/debugUnitTest/processDebugUnitTestJavaRes/out/numbertwo.json",
            expectedDataBaseFile.file
        )


        val databaseString = javaClass.classLoader!!
            .getResource("numbertwo.json")!!
            .readText()

        return databaseString
    }


    @Test
    fun decodeTest() {

        val importEntity :List<NumberTwoEntity> = Json.decodeFromString<List<NumberTwoEntity>>(takeTheFileFromGradle())

        importEntity.prepareForImport().forEach { we: NumberTwoEntity ->
            NumberTwoDAOImpl.insert(we)
        }

        val list: List<NumberTwoEntity> = NumberTwoDAOImpl.getAll()
        assertEquals(50, list.size)

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}