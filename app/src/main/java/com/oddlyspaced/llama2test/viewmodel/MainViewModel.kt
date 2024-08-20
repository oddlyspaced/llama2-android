package com.oddlyspaced.llama2test.viewmodel

import Llama2
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.oddlyspaced.llama2test.llama.InferenceResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timeInMs
import java.io.FileInputStream

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

    var prompt by mutableStateOf("Mary had a little lamb")
    var temperature by mutableFloatStateOf(0.5F)
    var steps by mutableIntStateOf(256)

    var isRunning by mutableStateOf(false)

    private val _results: SnapshotStateList<InferenceResult> = mutableStateListOf()
    val results: SnapshotStateList<InferenceResult> = _results

    fun getFileProps(context: Context, uri: Uri): String {
        return context.contentResolver.query(uri, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            "File: ${cursor.getString(nameIndex)}, Size: ${cursor.getLong(sizeIndex) / 1024.0 / 1024.0} MB"
        } ?: "Error in reading"
    }

    private fun getFileStream(context: Context, uri: Uri): FileInputStream? {
        return context.contentResolver.openInputStream(uri) as FileInputStream?
    }

    fun runModel(
        context: Context,
        temperature: Float = 0F,
        steps: Int = 256,
        prompt: String = "Mary had a little lamb",
        times: Int = 10,
    ) {
        if (tokenFileUri == null || modelFileUri == null) {
            Log.d(TAG, "One of file Uri is null!")
            return
        }

        isRunning = true
        CoroutineScope(Dispatchers.IO).launch {
            repeat(times) {
                val tokenizerFileStream = getFileStream(context, tokenFileUri!!)
                val modelFileStream = getFileStream(context, modelFileUri!!)
                val start = timeInMs()
                val model = Llama2(modelFileStream!!)
                val tokenize = Tokenizer.from(
                    model.config.vocabSize, tokenizerFileStream!!
                )
                val promptTokens: IntArray = tokenize.encode(prompt)
                val output = StringBuilder()
                model.generate(promptTokens, steps, temperature) { next ->
                    val tokenStr = tokenize.decode(next)
                    output.append(tokenStr)
                }
                val end = timeInMs()
                _results.add(
                    InferenceResult(
                        (steps) / (end - start).toDouble() * 1000,
                        output.toString()
                    )
                )
            }
            isRunning = false
        }

    }
}