package com.example.test2.features.exportimport.domain.repository

import com.example.test2.features.exportimport.domain.local.ImportUseCase
import kotlinx.coroutines.CoroutineDispatcher

interface ImportUseCaseRepository {

    fun initialize(importUseCase: ImportUseCase, dispatcher: CoroutineDispatcher)

    suspend fun invokeImport(databaseString: String) : Unit
}