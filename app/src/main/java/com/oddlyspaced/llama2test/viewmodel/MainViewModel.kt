package com.oddlyspaced.llama2test.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * @author : hardik
 * @created : 18/08/24, Sunday
 **/
class MainViewModel : ViewModel() {
    var tokenFileUri: Uri? by mutableStateOf(null)
    var modelFileUri: Uri? by mutableStateOf(null)

    fun getFileProps(context: Context, uri: Uri): String {
        return context.contentResolver.query(uri, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            "File: ${cursor.getString(nameIndex)}, Size: ${cursor.getLong(sizeIndex) / 1024.0 / 1024.0} MB"
        } ?: "Error in reading"
    }


}