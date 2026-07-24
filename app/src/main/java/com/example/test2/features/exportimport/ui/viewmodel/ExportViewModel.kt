package com.example.test2.features.exportimport.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test2.features.exportimport.domain.repository.ExportUseCaseRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class ExportViewModel(
    private val repository: ExportUseCaseRepositoryImpl
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    /**
     * Button 1
     * Store JSON file wherever.
     */
    fun exportToFile(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch {

            _loading.value = true

            val json = repository.invokeExport()

            context.contentResolver
                .openOutputStream(uri)
                ?.use { output ->
                    output.write(json.toByteArray())
                }

            _loading.value = false
        }
    }

    /**
     * Button 2
     * Make a temporal and open Share Sheet.
     */
    fun exportAndShare(
        context: Context
    ) {
        viewModelScope.launch {

            _loading.value = true

            val json = repository.invokeExport()

            val file = File(
                context.cacheDir,
                "backup.json"
            )

            file.writeText(json)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share backup"
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )

            _loading.value = false
        }
    }

    class Factory(
        private val repository: ExportUseCaseRepositoryImpl
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            return ExportViewModel(repository) as T
        }
    }
}