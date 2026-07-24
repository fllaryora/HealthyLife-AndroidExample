package com.example.test2.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.importer.domain.repository.ImportUseCaseRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ImportUseCaseRepositoryImpl
) : ViewModel() {

    fun importFromOtherApp(
        context: Context,
        uri: Uri
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            val databaseString =
                context.contentResolver
                    .openInputStream(uri)
                    ?.bufferedReader()
                    ?.use { it.readText() }
            databaseString?.let{
                repository.invokeImport(it)
            }

        }

    }

    class Factory(
        private val repository: ImportUseCaseRepositoryImpl
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return MainViewModel(repository) as T
        }
    }
}