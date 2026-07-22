package com.example.test2.features.exportimport.domain.repository

import com.example.test2.features.exportimport.domain.local.ExportUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

object ExportUseCaseRepositoryImpl : ExportUseCaseRepository {

    private lateinit var mExportUseCase: ExportUseCase
    private lateinit var ioDispatcher: CoroutineDispatcher
    private var isInit: Boolean = false
    override fun initialize(
        exportUseCase: ExportUseCase,
        dispatcher: CoroutineDispatcher
    ) {
        mExportUseCase = exportUseCase
        ioDispatcher = dispatcher
        isInit = true

    }

    override suspend fun invokeExport(): String {
        if(isInit) {

            return withContext(ioDispatcher) {
                val returnedValue = mExportUseCase.invokeExport()
                // TODO emit to other Repository when necesary screen refresh
                //invalidations.emit(Unit)
                return@withContext returnedValue
            }
        } else {
            throw Exception("ExportUseCaseRepositoryImpl Not init invokeExport")
        }
    }
}