package com.oddlyspaced.llama2test.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * @author : hardik
 * @created : 18/08/24, Sunday
 **/
class MainViewModel : ViewModel() {
    var tokenFilePath: String? by mutableStateOf(null)
    var modelFilePath: String? by mutableStateOf(null)
}