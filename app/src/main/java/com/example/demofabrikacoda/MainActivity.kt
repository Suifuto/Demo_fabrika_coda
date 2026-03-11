package com.example.demofabrikacoda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.demofabrikacoda.ui.theme.Demo_fabrika_codaTheme
import kotlinx.coroutines.flow.StateFlow
import org.peergos.HashedBlock
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            Demo_fabrika_codaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        name = "Подключение",
                        flow = viewModel.data,
                        viewModel,
                    )
                }
            }
        }
        viewModel.loadDataFromNetwork()
//        runBlocking<Unit> {
//            launch(Dispatchers.IO) {
//                println(Utils.getDataFromNode("",""))
//            }
//        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    name: String,
    flow: StateFlow<Result<String>?>,
    viewModel: MainViewModel,
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val data by flow.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = bottomPadding, top = topPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                viewModel.loadDataFromNetwork()
            },
            shape = RoundedCornerShape(15.dp),
            modifier = modifier.padding(10.dp),
        ) {
            Text("Запрос")
        }
        Text(
            text = "$data",
            modifier = modifier.padding(10.dp),
        )
    }
}

// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//    Demo_fabrika_codaTheme {
//        Greeting(name = "Android")
//    }
// }
