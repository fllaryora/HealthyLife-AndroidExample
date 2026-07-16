package com.example.test2.data

import com.example.test2.data.dao.implementations.PillDAO
import com.example.test2.data.entities.implementations.MyObjectBox
import com.example.test2.data.entities.implementations.Pill
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.config.DebugFlags
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.random.Random


open class PillTest {

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

        val mPillBox: Box<Pill> = store.boxFor(Pill::class.java)
        PillDAO.initialize(mPillBox)
    }

    @After
    fun tearDown() {
        _store?.close()
        _store = null
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }

    @Test
    fun fetchEmptyDataBaseShouldReturnZeroRecords() {
        val allList : List<Pill> = PillDAO.getPills()
        Assert.assertEquals(0, allList.size)
    }

    @Test
    fun insetWeightsAndGetTheData() {
        val supradin: Pill = Pill(0L, "Supradin Forte")
        val totalMagneciano: Pill = Pill(0L, "Total Magneciano")
        PillDAO.insert(supradin)
        PillDAO.insert(totalMagneciano)

        val allList : List<Pill> = PillDAO.getPills()
        Assert.assertEquals(2, allList.size)

    }

    @Test
    fun deleteAllEmptyTableShouldNotThrowException() {
        PillDAO.deleteAll()
    }

    @Test
    fun sparse() {
        val supradin: Pill = Pill(0L, "Supradin Forte")
        val totalMagneciano: Pill = Pill(0L, "Total Magneciano")
        PillDAO.insert(supradin)
        PillDAO.insert(totalMagneciano)

        var allList : List<Pill> = PillDAO.getPills()

        PillDAO.delete(allList.first())
        allList = PillDAO.getPills()

        Assert.assertEquals(1, allList.size)

    }

    companion object {
        private val TEST_DIRECTORY = File("objectbox-example/test-db")
    }
}