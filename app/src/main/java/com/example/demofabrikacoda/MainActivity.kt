package com.example.demofabrikacoda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demofabrikacoda.ui.theme.Demo_fabrika_codaTheme
import io.ipfs.cid.Cid
import org.peergos.HashedBlock

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
                    )
                }
            }
        }
        viewModel.loadDataFromNetwork(Utils.TEST_CID)
    }
}

@Composable
fun Greeting(
    model: MainViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val mainState by model.data.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(10.dp, 0.dp),
//                .padding(bottom = bottomPadding, top = topPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        EnterCidField {
            model.loadDataFromNetwork(it)
        }
        Text(
            text = mainState.status,
            modifier = modifier.padding(4.dp),
        )
        if (mainState.blocks.isNotEmpty()) {
            BlockList(
                mainState.blocks
            )
        }
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
                .height(intrinsicSize = IntrinsicSize.Max),
    ) {
        val cid = remember { mutableStateOf(Utils.TEST_CID) }

        TextField(
            value = cid.value,
            onValueChange = { newText -> cid.value = newText },
            singleLine = false,
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

@Suppress("ParamsComparedByRef")
@Composable
fun BlockList(
    cidBlocks: List<HashedBlock>
) {
    LazyColumn(
        modifier = Modifier
            .border(width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        items(cidBlocks) { cidBlock ->
            ItemListBlock(
                hash = cidBlock.hash?.toString() ?: "",
                block = cidBlock.block?.toString(Charsets.UTF_8)?.trim() ?: ""
            )
        }
    }
}

@Composable
fun ItemListBlock(
    hash: String,
    block: String
) {
    Row(
        modifier = Modifier
            .height(intrinsicSize = IntrinsicSize.Max)
            .padding(0.dp, 8.dp)
    ) {
        Text(
            modifier = Modifier.weight(2f),
            text = hash
        )
        VerticalDivider(
            color = Color.Red,
            modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp, 0.dp)
                .width(1.dp)
        )
        Text(
            modifier = Modifier.weight(3f),
            text = block
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

@Preview(showBackground = true)
@Composable
fun Preview() {
    Column {
        EnterCidField {}
        ItemListBlock(hash = "a", block = "b")
        BlockList(
            listOf(
                HashedBlock(Cid.decode(Utils.TEST_CID), byteArrayOf(0, 1)),
                HashedBlock(Cid.decode(Utils.TEST_CID), byteArrayOf(1, 2)),
            )
        )
    }
}
