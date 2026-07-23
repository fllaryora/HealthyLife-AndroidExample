package com.example.test2.pill.data.local

import com.example.test2.data.entities.behaviors.prepareForImport
import com.example.test2.exportimport.domain.local.assertEndsWith
import com.example.test2.features.MyObjectBox
import com.example.test2.features.exportimport.domain.local.jsonPropertiesForExport
import com.example.test2.features.pill.data.local.PillDAOImpl
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.water.data.local.WaterDAOImpl
import com.example.test2.features.water.data.local.WaterEntity
import io.objectbox.Box
import io.objectbox.BoxStore
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
import java.net.URL

open class PillEntityTest {

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

        val mPillEntityBox: Box<PillEntity> = store.boxFor(PillEntity::class.java)
        PillDAOImpl.initialize(mPillEntityBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        val allList : List<PillEntity> = PillDAOImpl.getPills()
        Assert.assertEquals(0, allList.size)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)

        val allList : List<PillEntity> = PillDAOImpl.getPills()
        Assert.assertEquals(2, allList.size)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        PillDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)

        var allList : List<PillEntity> = PillDAOImpl.getPills()

        PillDAOImpl.delete(allList.first())
        allList = PillDAOImpl.getPills()

        Assert.assertEquals(1, allList.size)

    }

    @Test
    fun encodeTest() {
        val name1 = "Supradin Forte"
        val supradin: PillEntity = PillEntity(0L, name1)
        val name2 = "Total Magneciano"
        val totalMagneciano: PillEntity = PillEntity(0L, name2)
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)

        val allList : List<PillEntity> = PillDAOImpl.getPills()

        val prettyJson:String = jsonPropertiesForExport.encodeToString(allList)

        val jsonElement = Json.parseToJsonElement(prettyJson)

        assertEquals(2, jsonElement.jsonArray.size)

        jsonElement.jsonArray.forEach { element: JsonElement ->
            val currentObj: JsonObject = element.jsonObject
            assertTrue(currentObj.containsKey("id"))
            assertTrue(currentObj.containsKey("name"))
            val currentName: String? = currentObj["name"]?.jsonPrimitive?.content
            val currentId: Long? = currentObj["id"]?.jsonPrimitive?.long

            when (currentId) {
                1L -> assertEquals(name1, currentName)
                2L -> assertEquals(name2, currentName)
            }
        }

    }

    private fun takeTheFileFromGradle() : String {
        val expectedDataBaseFile: URL = javaClass.classLoader!!
            .getResource("pills.json")

        /*This part is a crapy part
        because it will fail outside gradle world
        * */
        println(expectedDataBaseFile.file)
        assertEndsWith(
            "The path of the resource file",
            "app/build/intermediates/java_res/debugUnitTest/processDebugUnitTestJavaRes/out/pills.json",
            expectedDataBaseFile.file
        )


        val databaseString = javaClass.classLoader!!
            .getResource("pills.json")!!
            .readText()

        return databaseString
    }


    @Test
    fun decodeTest() {

        val importEntity :List<PillEntity> = Json.decodeFromString<List<PillEntity>>(takeTheFileFromGradle())

        importEntity.prepareForImport().forEach { we: PillEntity ->
            PillDAOImpl.insert(we)
        }

        val list: List<PillEntity> = PillDAOImpl.getPills()
        Assert.assertEquals(2, list.size)

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}