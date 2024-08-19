package com.oddlyspaced.llama2test.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oddlyspaced.llama2test.ui.theme.Llama2TestTheme
import com.oddlyspaced.llama2test.viewmodel.MainViewModel
import kotlinx.coroutines.flow.forEach
import kotlin.math.round
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Llama2TestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(PaddingValues(start = 16.dp, end = 16.dp))
                    ) {
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
                        RunModel()
                        ModelResults()
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

    Column(modifier.padding(PaddingValues(top = 12.dp))) {
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
fun RunModel(modifier: Modifier = Modifier) {
    val viewModel = viewModel<MainViewModel>()
    val context = LocalContext.current

    Column(modifier.padding(PaddingValues(top = 16.dp))) {
        TextField(
            value = viewModel.prompt,
            onValueChange = { viewModel.prompt = it },
            label = { Text("Prompt") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            viewModel.runModel(context, 0F, 256, viewModel.prompt, 10)
        }, modifier = modifier.padding(PaddingValues(top = 8.dp))) {
            Text("Run Model")
        }
    }
}

@Composable
fun ModelResults(modifier: Modifier = Modifier) {
    val viewModel = viewModel<MainViewModel>()

    var showDialog by remember {
        mutableStateOf(false)
    }
    var dialogText by remember {
        mutableStateOf("")
    }

    if (showDialog) {
        MinimalDialog(dialogText, onDismissRequest = {
            showDialog = false
        })
    }

    Column(modifier.padding(PaddingValues(top = 16.dp))) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Results")
            if (viewModel.isRunning) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        LazyColumn {
            itemsIndexed(viewModel.results) { index, result ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            dialogText = result.result
                            showDialog = true
                        }),
                ) {
                    Text(text = "Results ${index + 1}")
                    Text(
                        (round(result.time * 100) / 100).toString() + " token/s",
                    )
                }
            }
        }
    }
}

@Composable
fun MinimalDialog(text: String, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}
