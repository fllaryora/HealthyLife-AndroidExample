package com.example.test2.features.importer.domain.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.importer.domain.repository.ImportUseCaseRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImportViewModel(
    private val repository: ImportUseCaseRepositoryImpl
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun importAction(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {

            _loading.value = true

            val databaseString : String=
                context.contentResolver
                    .openInputStream(uri)
                    ?.bufferedReader()
                    ?.use { it.readText() }
                    ?: ""

            repository.invokeImport(
                databaseString
            )

            _loading.value = false
        }
    }

    class Factory(
        private val repository: ImportUseCaseRepositoryImpl
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return ImportViewModel(repository) as T
        }
    }
}