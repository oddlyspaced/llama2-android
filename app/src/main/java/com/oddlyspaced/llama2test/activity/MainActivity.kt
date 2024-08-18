package com.oddlyspaced.llama2test.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.unit.dp
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
                            onClickSelect = { /*TODO*/ },
                            subtitle = vm.modelFilePath ?: "Nothing Selected"
                        )
                        FilePicker(
                            title = "Pick Tokenizer File",
                            buttonText = "Select",
                            onClickSelect = { /*TODO*/ },
                            subtitle = vm.tokenFilePath ?: "Nothing Selected"
                        )
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
    onClickSelect: () -> Unit,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(PaddingValues(start = 16.dp, top = 12.dp))) {
        Text(title)
        Button(onClick = onClickSelect, modifier = Modifier.padding(PaddingValues(top = 4.dp))) {
            Text(buttonText)
        }
        Text(text = subtitle, modifier = Modifier.padding(PaddingValues(top = 4.dp)))
    }
}