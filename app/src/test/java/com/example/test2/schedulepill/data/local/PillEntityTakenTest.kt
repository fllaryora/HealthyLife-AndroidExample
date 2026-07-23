package com.example.test2.schedulepill.data.local

import com.example.test2.TestDateFactory
import com.example.test2.data.converter.TimeConverter
import com.example.test2.features.MyObjectBox
import com.example.test2.features.exportimport.domain.local.jsonPropertiesForExport
import com.example.test2.features.pill.data.local.PillDAOImpl
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.recordpill.data.local.PillTakenDAOImpl
import com.example.test2.features.recordpill.data.local.PillTakenEntity
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
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.random.Random

open class PillEntityTakenTest {

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

        val mPillEntityBox: Box<PillEntity> = store.boxFor(PillEntity::class.java)
        PillDAOImpl.initialize(mPillEntityBox)
        val mPillTakenEntityBox: Box<PillTakenEntity> = store.boxFor(PillTakenEntity::class.java)
        PillTakenDAOImpl.initialize(mPillTakenEntityBox)

    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)
        val pillEntityList : List<PillEntity> = PillDAOImpl.getPills()


        val allList : List<PillTakenEntity> = PillTakenDAOImpl.getAll()
        Assert.assertEquals(0, allList.size)
        val allTakenPill : List<PillTakenEntity> = PillTakenDAOImpl.getAllByPill(pillEntityList.first())
        Assert.assertEquals(0, allTakenPill.size)
        val allTakenPillLimited : List<PillTakenEntity> = PillTakenDAOImpl.getPillTakenList(pillEntityList.first(), 0L, 20L)
        Assert.assertEquals(0, allTakenPillLimited.size)
        val allTakenPillLimitedPair : Pair<List<PillTakenEntity>,Float?> = PillTakenDAOImpl.getPillTaken(pillEntityList.first(), 0L, 20L)
        Assert.assertEquals(0, allTakenPillLimitedPair.first.size)
        Assert.assertEquals(null, allTakenPillLimitedPair.second)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)
        val pillEntityList : List<PillEntity> = PillDAOImpl.getPills()


        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )

        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                pillEntityList.forEach { pillEntity : PillEntity ->
                    val pillTakenEntity = PillTakenEntity.create(pillEntityAsociated = pillEntity, date = someDayAtTheMorning, )

                    PillTakenDAOImpl.insert(
                        pillTakenEntity
                    )
                }
            }

        val allList : List<PillTakenEntity> = PillTakenDAOImpl.getAll()
        Assert.assertEquals(72, allList.size)
        val allTakenPill : List<PillTakenEntity> = PillTakenDAOImpl.getAllByPill(pillEntityList.first())
        Assert.assertEquals(36, allTakenPill.size)
        val allTakenPillLimited : List<PillTakenEntity> = PillTakenDAOImpl.getPillTakenList(pillEntityList.first(), 0L, 20L)
        Assert.assertEquals(20, allTakenPillLimited.size)
        val allTakenPillLimitedPair : Pair<List<PillTakenEntity>,Float?> = PillTakenDAOImpl.getPillTaken(pillEntityList.first(), 0L, 20L)
        Assert.assertEquals(20, allTakenPillLimitedPair.first.size)
        Assert.assertEquals(488818.0f, allTakenPillLimitedPair.second)


    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        PillTakenDAOImpl.deleteAll()
    }

    @Test
    fun sparse() {
        val supradin: PillEntity = PillEntity(0L, "Supradin Forte")
        val totalMagneciano: PillEntity = PillEntity(0L, "Total Magneciano")
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)
        val pillEntityList : List<PillEntity> = PillDAOImpl.getPills()

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                pillEntityList.forEach { pillEntity : PillEntity ->
                    val pillTakenEntity = PillTakenEntity.create(pillEntityAsociated = pillEntity, date = someDayAtTheMorning, )

                    PillTakenDAOImpl.insert(
                        pillTakenEntity
                    )
                }
            }

        val touch = coin(0, pillEntityList.size - 1)
        pillEntityList.forEachIndexed { index, pillEntity : PillEntity ->
            if( index != touch) {
                PillTakenDAOImpl.deleteByPill(pillEntity)
                PillDAOImpl.delete(pillEntity)
            } else {
                val listTaken = PillTakenDAOImpl.getAllByPill(pillEntity)
                val touchTaken = coin(0, listTaken.size - 1)
                listTaken.forEachIndexed { indexTaken, taken : PillTakenEntity ->
                    if( indexTaken != touchTaken) {
                        PillTakenDAOImpl.delete(taken)
                    }
                }
            }
        }

        val allList : List<PillTakenEntity> = PillTakenDAOImpl.getAll()
        Assert.assertEquals(1, allList.size)
        val allTakenPillFirst : List<PillTakenEntity> = PillTakenDAOImpl.getAllByPill(pillEntityList.first())
        val allTakenPillSecond : List<PillTakenEntity> = PillTakenDAOImpl.getAllByPill(pillEntityList[1])

        Assert.assertEquals(1, (allTakenPillFirst.size + allTakenPillSecond.size) )

        val allTakenPillLimitedFirst : List<PillTakenEntity> = PillTakenDAOImpl.getPillTakenList(pillEntityList.first(), 0L, 20L)
        val allTakenPillLimitedScond : List<PillTakenEntity> = PillTakenDAOImpl.getPillTakenList(pillEntityList[1], 0L, 20L)

        Assert.assertEquals(1, (allTakenPillLimitedFirst.size + allTakenPillLimitedScond.size) )

        val allTakenPillLimitedPairFirst : Pair<List<PillTakenEntity>,Float?> = PillTakenDAOImpl.getPillTaken(pillEntityList.first(), 0L, 20L)
        val allTakenPillLimitedPairSecond : Pair<List<PillTakenEntity>,Float?> = PillTakenDAOImpl.getPillTaken(pillEntityList[1], 0L, 20L)

        Assert.assertEquals(1, (allTakenPillLimitedPairFirst.first.size + allTakenPillLimitedPairSecond.first.size) )

        if (allTakenPillLimitedPairFirst.first.size == 1) {
            Assert.assertEquals(null, allTakenPillLimitedPairSecond.second )
            val firstTake: Float? = TimeConverter.convertISOToHours(allTakenPillLimitedPairFirst.first.firstOrNull()?.getTime())
            Assert.assertEquals(firstTake, allTakenPillLimitedPairFirst.second  )

        }
        if (allTakenPillLimitedPairSecond.first.size == 1) {
            Assert.assertEquals(null, allTakenPillLimitedPairFirst.second )
            val firstTake: Float? = TimeConverter.convertISOToHours(allTakenPillLimitedPairSecond.first.firstOrNull()?.getTime())
            Assert.assertEquals(firstTake, allTakenPillLimitedPairSecond.second)
        }


    }

    @Test
    fun encodeTest() {
        val name1 = "Supradin Forte"
        val supradin: PillEntity = PillEntity(0L, name1)
        val name2 = "Total Magneciano"
        val totalMagneciano: PillEntity = PillEntity(0L, name2)
        PillDAOImpl.insert(supradin)
        PillDAOImpl.insert(totalMagneciano)

        val pillEntityList : List<PillEntity> = PillDAOImpl.getPills()
        assertEquals(2, pillEntityList.size)

        val dates : Sequence<OffsetDateTime> = TestDateFactory.dailySequence(
            2025, 10, 25, 14, 30, 0, 0,
        )
        dates.take(36)
            .forEach { someDayAtTheMorning: OffsetDateTime ->
                pillEntityList.forEach { pillEntity : PillEntity ->
                    val pillTakenEntity = PillTakenEntity.create(pillEntityAsociated = pillEntity, date = someDayAtTheMorning, )

                    PillTakenDAOImpl.insert(
                        pillTakenEntity
                    )
                }
            }

        val allList : List<PillTakenEntity> = PillTakenDAOImpl.getAll()
        assertEquals(2*36, allList.size)

        val prettyJson:String = jsonPropertiesForExport.encodeToString(allList)

        print(prettyJson)

        val jsonElement = Json.parseToJsonElement(prettyJson)

        assertEquals(2*36, jsonElement.jsonArray.size)

        jsonElement.jsonArray.forEach { element: JsonElement ->
            val currentObj: JsonObject = element.jsonObject
            assertTrue(currentObj.containsKey("id"))
            assertTrue(currentObj.containsKey("date"))
            assertTrue(currentObj.containsKey("isTaken"))
            assertTrue(currentObj.containsKey("exportPillId")) // falla aca

            /*
             val currentName: String? = currentObj["name"]?.jsonPrimitive?.content
             val currentId: Long? = currentObj["id"]?.jsonPrimitive?.long

             when (currentId) {
                 1L -> assertEquals(name1, currentName)
                 2L -> assertEquals(name2, currentName)
             }*/
        }

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}