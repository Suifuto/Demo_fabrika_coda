package com.example.demofabrikacoda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.demofabrikacoda.ui.theme.Demo_fabrika_codaTheme
import kotlinx.coroutines.flow.StateFlow


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
        viewModel.loadDataFromNetwork(Utils.TEST_CID)
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
        modifier =
            modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding, top = topPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        EnterCidField {
            viewModel.loadDataFromNetwork(it)
        }
        Text(
            text = "$data",
            modifier = modifier.padding(10.dp),
        )
    }
}

@Composable
fun EnterCidField(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    Row(
        modifier =
            modifier
                .padding(10.dp)
                .height(intrinsicSize = IntrinsicSize.Max),
    ) {
        val cid = remember { mutableStateOf(Utils.TEST_CID) }

        TextField(
            value = cid.value,
            onValueChange = { newText -> cid.value = newText },
            singleLine = false,
            maxLines = 5,
            shape =
                RoundedCornerShape(
                    topStart = CornerSize(15.dp),
                    topEnd = CornerSize(0),
                    bottomEnd = CornerSize(0),
                    bottomStart = CornerSize(15.dp),
                ),
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier.weight(4f),
        )
        Button(
            onClick = {
                onClick(cid.value)
            },
            shape =
                RoundedCornerShape(
                    topStart = CornerSize(0),
                    topEnd = CornerSize(15.dp),
                    bottomEnd = CornerSize(15.dp),
                    bottomStart = CornerSize(0),
                ),
            modifier =
                Modifier
                    .weight(2f)
                    .fillMaxHeight(),
        ) {
            Text(
                text = "Запрос",
                fontSize = 16.sp,
            )
        }
    }
}

// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//    Demo_fabrika_codaTheme {
//        Greeting(name = "Android")
//    }
// }

@Preview
@Composable
fun CustomizedTextFieldPreview() {
    EnterCidField {}
}
