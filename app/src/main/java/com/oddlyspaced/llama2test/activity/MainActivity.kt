package com.oddlyspaced.llama2test.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oddlyspaced.llama2test.ui.theme.Llama2TestTheme
import com.oddlyspaced.llama2test.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Llama2TestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        FilePicker(
                            title = "Pick Model File",
                            buttonText = "Select",
                            onFileSelect = {
                                vm.modelFileUri = it
                            },
//                            subtitle =  vm.modelFileUri?.toString() ?: "Nothing Selected"
                            subtitle = vm.modelFileUri?.let {
                                vm.getFileProps(
                                    applicationContext,
                                    it
                                )
                            } ?: "Nothing Selected"
                        )
                        FilePicker(
                            title = "Pick Tokenizer File",
                            buttonText = "Select",
                            onFileSelect = {
                                vm.tokenFileUri = it
                            },
                            vm.tokenFileUri?.let {
                                vm.getFileProps(
                                    applicationContext,
                                    it
                                )
                            } ?: "Nothing Selected"
                        )
                        RunModel(modifier = Modifier)
                    }
                }
            }
        }
    }
}

@Composable
fun FilePicker(
    title: String,
    buttonText: String,
    onFileSelect: (Uri?) -> Unit,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val filePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            onFileSelect(it)
        }

    Column(modifier.padding(PaddingValues(start = 16.dp, top = 12.dp))) {
        Text(title)
        Button(
            onClick = { filePickerLauncher.launch("*/*") },
            modifier = Modifier.padding(PaddingValues(top = 4.dp))
        ) {
            Text(buttonText)
        }
        Text(text = subtitle, modifier = Modifier.padding(PaddingValues(top = 4.dp)))
    }
}

@Composable
fun RunModel(modifier: Modifier) {
    val viewModel = viewModel<MainViewModel>()
    val context = LocalContext.current
    Button(onClick = {
        viewModel.runModel(context)
//        Log.d("TESTING", viewModel.modelFileUri.toString())
    }) {
        Text("Run Model")
    }
}