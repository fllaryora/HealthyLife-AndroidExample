package com.example.test2.weight.data.local

import com.example.test2.TestDateFactory
import com.example.test2.features.MyObjectBox
import com.example.test2.features.exportimport.domain.local.jsonPropertiesForExport
import com.example.test2.features.weight.data.local.WeightDAOImpl

import com.example.test2.features.weight.data.local.WeightEntity
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.random.Random


open class WeightDAOImpTest {

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

        val mWeightEntityBox: Box<WeightEntity> = store.boxFor(WeightEntity::class.java)
        WeightDAOImpl.initialize(mWeightEntityBox)
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
        val list: List<WeightEntity> = WeightDAOImpl.getWeights(0L, 20L)
        val allList : List<WeightEntity> = WeightDAOImpl.getAll()
        val pair : Pair<List<WeightEntity>, Float?> = WeightDAOImpl.getWeightsAndFirstDay(0L, 20L)
        Assert.assertEquals(0, list.size)
        Assert.assertEquals(0, allList.size)
        Assert.assertEquals(0, pair.first.size)
        Assert.assertEquals(null, pair.second)
    }

    @Test
    fun insetWeightsAndGetTheData() {

        TestDateFactory.buildWeights(2025, 10, 25, 14, 30, 0, 0,
            36
        ).forEach { we: WeightEntity ->
            WeightDAOImpl.insert(we)
        }

        val list: List<WeightEntity> = WeightDAOImpl.getWeights(0L, 20L)
        val allList : List<WeightEntity> = WeightDAOImpl.getAll()
        val pair : Pair<List<WeightEntity>, Float?> = WeightDAOImpl.getWeightsAndFirstDay(0L, 20L)
        Assert.assertEquals(20, list.size)
        Assert.assertEquals(36, allList.size)
        Assert.assertEquals(20, pair.first.size)
        Assert.assertEquals(488818.0f, pair.second)
    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        WeightDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {

        TestDateFactory.buildWeights(2025, 10, 25, 14, 30, 0, 0,
            36
        ).forEach { we: WeightEntity ->
            WeightDAOImpl.insert(we)
        }

        var allList = WeightDAOImpl.getAll()
        val touch : Int  = coin(0, allList.size-1)
        allList.forEachIndexed { index, weightEntity : WeightEntity ->
            if( index != touch){
                WeightDAOImpl.delete(weightEntity)
            }
        }

        allList = WeightDAOImpl.getAll()

        Assert.assertEquals(1, allList.size)

    }

    @Test
    fun encodeTest() {

        TestDateFactory.buildWeights(2025, 10, 25, 14, 30, 0, 0,
            36
        ).forEach { we: WeightEntity ->
            WeightDAOImpl.insert(we)
        }

        val list: List<WeightEntity> = WeightDAOImpl.getAll()

        val prettyJson:String = jsonPropertiesForExport.encodeToString(list)
        println(prettyJson)
        val jsonElement: JsonElement = Json.parseToJsonElement(prettyJson)

        jsonElement.jsonArray.forEach { element: JsonElement ->
            val obj: JsonObject = element.jsonObject
            assertTrue(obj.containsKey("id"))
            assertTrue(obj.containsKey("date"))
            assertTrue(obj.containsKey("weight"))
        }
    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}