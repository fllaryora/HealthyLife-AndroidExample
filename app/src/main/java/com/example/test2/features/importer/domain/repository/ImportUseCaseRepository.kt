package com.example.test2.features.importer.domain.repository

import com.example.test2.features.importer.domain.local.ImportUseCase
import kotlinx.coroutines.CoroutineDispatcher

interface ImportUseCaseRepository {

    fun initialize(importUseCase: ImportUseCase, dispatcher: CoroutineDispatcher)

    suspend fun invokeImport(databaseString: String) : Unit
}