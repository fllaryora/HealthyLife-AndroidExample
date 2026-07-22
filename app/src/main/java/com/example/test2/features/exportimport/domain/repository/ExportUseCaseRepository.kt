package com.example.test2.features.exportimport.domain.repository


import com.example.test2.features.exportimport.domain.local.ExportUseCase
import kotlinx.coroutines.CoroutineDispatcher

interface ExportUseCaseRepository {

    fun initialize(exportUseCase: ExportUseCase, dispatcher: CoroutineDispatcher)

    suspend fun invokeExport():  String
}