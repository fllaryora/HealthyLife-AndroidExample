package com.example.test2.features.exportimport.domain.repository

import com.example.test2.features.exportimport.domain.local.ImportUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

object ImportUseCaseRepositoryImpl : ImportUseCaseRepository {
    private lateinit var mImportUseCase: ImportUseCase
    private lateinit var ioDispatcher: CoroutineDispatcher
    private var isInit: Boolean = false

    override fun initialize(
        importUseCase: ImportUseCase,
        dispatcher: CoroutineDispatcher
    ) {
        mImportUseCase = importUseCase
        ioDispatcher = dispatcher
        isInit = true
    }

    override suspend fun invokeImport(databaseString: String) {
        if(isInit) {
            return withContext(ioDispatcher) {
                val returnedValue = mImportUseCase.invokeImport(databaseString)
                // TODO emit to other Repository when necesary screen refresh
                //invalidations.emit(Unit)
                return@withContext returnedValue
            }
        } else {
            throw Exception("ImportUseCaseRepositoryImpl Not init invokeImport")
        }
    }
}