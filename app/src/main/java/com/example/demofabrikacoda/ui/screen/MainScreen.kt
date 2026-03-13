package com.example.demofabrikacoda.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.demofabrikacoda.MainViewModel
import com.example.demofabrikacoda.data.AppConst
import com.example.demofabrikacoda.data.ControllAction
import com.example.demofabrikacoda.data.PingModel
import com.example.demofabrikacoda.toConvertAndFilter
import io.ipfs.cid.Cid
import org.peergos.HashedBlock
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(
    model: MainViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val cidDataHistory by model.data.collectAsState()
    val status by model.status.collectAsState()
    val pingHistory by model.dataPing.collectAsState()
    val statusPing by model.statusPing.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(10.dp, 0.dp),
//                .padding(bottom = bottomPadding, top = topPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        PingView(
            model = model,
            statusPing = statusPing,
            pingHistory = pingHistory
        ) { action ->
            when (action) {
                is ControllAction.Start -> {
                    model.startPing()
                }
                is ControllAction.Pause -> {}
                is ControllAction.Stop -> {
                    model.stopPing()
                }
            }
        }
        ControllPanel(name = "Управление подключением к ноде") { action ->
            when (action) {
                is ControllAction.Start -> {
                    model.startIpfs()
                }
                is ControllAction.Pause -> {}
                is ControllAction.Stop -> {
                    model.stopIpfs()
                }
            }

        }
        EnterCidField {
            model.loadDataFromNetwork(it)
        }
        if (!status.isNullOrBlank()) {
            Text(
                text = status ?: "Ошибка получения статуса программы",
                modifier = modifier.padding(2.dp),
            )
        }
        if (cidDataHistory.isNotEmpty()) {
            BlockList(
                cidDataHistory
            )
        }
    }
}

@Composable
fun PingHistoryView(
    pingHistory: List<PingModel>
) {
    if (pingHistory.isEmpty()) return

    val max = pingHistory.maxOfOrNull { it.latency ?: 0L } ?: 0L
    val min = pingHistory.minOfOrNull { it.latency ?: 0L } ?: 0L
    val jitter = max - min

    val average = pingHistory.map { it.latency ?: 0L }.average()

    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.CEILING
    df.format(average)

    var viewTimestamp = ""
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    pingHistory[pingHistory.lastIndex].timestamp?.let {
        viewTimestamp = it
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$viewTimestamp"
        )
        Text(
            text = "тек. ${pingHistory[pingHistory.lastIndex].latency} мс"
        )
        Text(
            text = "ср. ${df.format(average)} мс"
        )
        Text(
            text = "виб. ${df.format(jitter)} мс"
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
                .height(intrinsicSize = IntrinsicSize.Max),
    ) {
        val cid = remember { mutableStateOf(AppConst.TEST_CID) }

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

@Composable
fun PingView(
    model: MainViewModel,
    statusPing: String?,
    pingHistory: List<PingModel>,
    onClick: (ControllAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(2.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ControllPanel(name = "Управление пинг") { onClick(it) }
        if (!statusPing.isNullOrBlank()) {
            Text(
                text = statusPing ?: "Ошибка получения статуса пинга",
                fontSize = 16.sp,
                color = Color.Red,
                modifier = Modifier.padding(4.dp),
            )
        } else {
            PingHistoryView(pingHistory = pingHistory)
        }
    }
}

@Composable
fun ControllPanel(
    name: String,
    onClick: (ControllAction) -> Unit
) {
    Text(text = name)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(2.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
        Button(
            onClick = {
                onClick(ControllAction.Start)
            },
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = "Старт",
                fontSize = 16.sp,
            )
        }

//        Button(
//            onClick = {
//                onClick(PingControllPanelAction.Pause)
//            },
//            shape = RoundedCornerShape(8.dp),
//        ) {
//            Text(
//                text = "Пауза",
//                fontSize = 16.sp,
//            )
//        }

        Button(
            onClick = {
                onClick(ControllAction.Stop)
            },
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = "Стоп",
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
    Text(text = "CID Блоки")
    LazyColumn(
        modifier = Modifier
            .border(width = 1.dp, color = Color.DarkGray, shape = RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        items(cidBlocks) { cidBlock ->
            ItemListBlock(
                hash = cidBlock.hash?.toString() ?: "",
                block = cidBlock.block?.toConvertAndFilter() ?: ""

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

@Preview(showBackground = true)
@Composable
fun Preview() {
    Column {
        EnterCidField {}
        ItemListBlock(hash = "a", block = "b")
        BlockList(
            listOf(
                HashedBlock(Cid.decode(AppConst.TEST_CID), byteArrayOf(0, 1)),
                HashedBlock(Cid.decode(AppConst.TEST_CID), byteArrayOf(1, 2)),
            )
        )
        PingHistoryView(
            listOf(
                PingModel(null, latency = 10L),
                PingModel( null, latency = 20L),
                PingModel( null, latency = 40L),
                PingModel( null, latency = 80L),
                PingModel( null, latency = 120L),
            )
        )
    }
}
