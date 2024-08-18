package com.oddlyspaced.llama2test.viewmodel

import Llama2
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timeInMs
import java.io.FileInputStream
import java.io.InputStream

/**
 * @author : hardik
 * @created : 18/08/24, Sunday
 **/
class MainViewModel : ViewModel() {

    companion object {
        const val TAG = "MainViewModel"
    }

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

    fun getFileStream(context: Context, uri: Uri): FileInputStream? {
        return context.contentResolver.openInputStream(uri) as FileInputStream?
    }

    fun runModel(
        context: Context,
        temperature: Float = 0F,
        steps: Int = 256,
        prompt: String = "Mary had a little lamb"
    ) {
        if (tokenFileUri == null || modelFileUri == null) {
            Log.d(TAG, "One of file Uri is null!")
            return
        }
        val tokenizerFileStream = getFileStream(context, tokenFileUri!!)
        val modelFileStream = getFileStream(context, modelFileUri!!)

        if (tokenizerFileStream == null || modelFileStream == null) {
            Log.d(TAG, "One input sream null")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val start = timeInMs()
            Log.d(TAG, "Step 1")
            val model = Llama2(modelFileStream)
            Log.d(TAG, "Step 2")
            val tokenize = Tokenizer.from(
                model.config.vocabSize, tokenizerFileStream
            )
            Log.d(TAG, "Step 3")
            val promptTokens: IntArray = tokenize.encode(prompt)
            Log.d(TAG, "Step 4")
            val output = StringBuilder()
            Log.d(TAG, "Step 5")
            model.generate(promptTokens, steps, temperature) { next ->
                val tokenStr = tokenize.decode(next)
                output.append(tokenStr)
            }
            Log.d("TESTING_MODEL", output.toString())
            val end = timeInMs()
            Log.d(TAG, "\nachieved tok/s: ${(steps) / (end - start).toDouble() * 1000}\n")
        }

    }
}