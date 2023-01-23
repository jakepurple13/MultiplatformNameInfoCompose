package com.programmersbox.shared

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

@Composable
internal fun App(isDarkMode: Boolean = isSystemInDarkTheme()) {
    MaterialTheme(
        colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
    ) {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val scope = rememberCoroutineScope()
                NameInfoCompose(
                    vm = remember { NameInfoViewModel(scope) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NameInfoCompose(vm: NameInfoViewModel) {
    Scaffold(
        topBar = {
            OutlinedTextField(
                value = vm.name,
                onValueChange = { vm.name = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                trailingIcon = { IconButton(onClick = vm::getInfo) { Icon(Icons.Default.Check, null) } },
                keyboardActions = KeyboardActions(onDone = { vm.getInfo() }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                maxLines = 1,
                label = { Text("Enter Name:") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FirstRow(vm)
            SecondRow(vm)
            Divider()
            Recent(vm)
        }
    }
}

@Composable
internal fun FirstRow(vm: NameInfoViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ElevatedCard(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = vm.state) { state ->
                when (state) {
                    NetworkState.Loading -> {
                        CircularProgressIndicator()
                    }

                    NetworkState.NotLoading -> {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(
                                vm.ifyInfo.name,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                "Age: ${vm.ifyInfo.age}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        ElevatedCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val gender by remember {
                    derivedStateOf { vm.ifyInfo.gender }
                }
                Circle(
                    progress = gender?.probability ?: 0f,
                    strokeColor = animateColorAsState(gender?.genderColor ?: MaterialTheme.colorScheme.primary).value,
                    backgroundColor = animateColorAsState(
                        gender?.genderColorInverse ?: MaterialTheme.colorScheme.background
                    ).value,
                    textColor = animateColorAsState(gender?.genderColor ?: MaterialTheme.colorScheme.primary).value,
                    modifier = Modifier
                        .size(90.dp)
                        .padding(4.dp)
                )
                Text(gender?.capitalGender().orEmpty())
            }
        }

    }
}

@Composable
internal fun SecondRow(vm: NameInfoViewModel) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(vm.ifyInfo.nationality) { country ->
            ElevatedCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    //var palette by remember { mutableStateOf<Palette?>(null) }

                    Circle(
                        progress = animateFloatAsState(country.probability * 100).value,
                        strokeColor = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(90.dp)
                            .padding(4.dp)
                    )

                    NetworkImage(
                        country.flagUrl,
                        modifier = Modifier.size(24.dp)
                    )

                    Text(country.country_id)
                    /*GlideImage(
                        modifier = Modifier.size(24.dp),
                        imageModel = { country.flagUrl },
                        component = rememberImageComponent {
                            +PalettePlugin(
                                imageModel = country.flagUrl,
                                useCache = true,
                                paletteLoadedListener = { palette = it }
                            )
                        }
                    )

                    Text(country.countryName)*/
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Recent(vm: NameInfoViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(175.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(vm.recent) { r ->
            ElevatedCard(onClick = { vm.onRecentPress(r) }) {
                Row(modifier = Modifier.padding(4.dp)) {

                    Column {

                        Text(
                            r.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            "Age: ${r.age}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        r.nationality.take(3).forEach {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                NetworkImage(
                                    it.flagUrl,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text("${(it.probability * 100).roundToInt()}%")
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val gender by remember {
                            derivedStateOf { r.gender }
                        }
                        Circle(
                            progress = gender?.probability ?: 0f,
                            strokeColor = gender?.genderColor ?: MaterialTheme.colorScheme.primary,
                            backgroundColor = gender?.genderColorInverse ?: MaterialTheme.colorScheme.background,
                            modifier = Modifier
                                .size(90.dp)
                                .padding(4.dp)
                        )
                        Text(gender?.capitalGender().orEmpty())
                    }

                    Column {
                        IconButton(onClick = { vm.onRemovePress(r) }) { Icon(Icons.Default.Clear, null) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
internal fun Circle(
    progress: Float,
    modifier: Modifier = Modifier,
    max: Int = 100,
    strokeWidth: Dp = 8.dp,
    strokeColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = strokeColor,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    textStyle: TextStyle = TextStyle(color = textColor)
) {
    val progressAnimated by animateFloatAsState(progress)
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult =
        textMeasurer.measure(text = AnnotatedString("${progressAnimated.roundToInt()}%"))
    val textSize = textLayoutResult.size
    Canvas(modifier) {
        drawCircle(
            color = backgroundColor,
            radius = size.minDimension / 2,
            style = Stroke(strokeWidth.value)
        )

        drawArc(
            color = strokeColor,
            useCenter = false,
            startAngle = 270f,
            sweepAngle = progressAnimated / max * 360,
            style = Stroke(strokeWidth.value, cap = StrokeCap.Round),
        )

        drawText(
            textMeasurer = textMeasurer,
            text = "${progressAnimated.roundToInt()}%",
            style = textStyle,
            topLeft = Offset(
                (size.width - textSize.width) / 2f,
                (size.height - textSize.height) / 2f
            )
        )
    }
}

internal class NameInfoViewModel(private val scope: CoroutineScope) {

    private val service = ApiService()
    var state by mutableStateOf(NetworkState.NotLoading)
    val recent = mutableStateListOf<IfyInfo>()
    var name by mutableStateOf("")
    var ifyInfo by mutableStateOf(
        IfyInfo(
            name = "Name",
            gender = Gender(gender = "male", probability = 50f),
            age = 50,
        )
    )

    private val db by lazy { IfyInfoDatabase(scope) }

    private var start = true

    init {
        /*db.nameInfoDao()
            .getAll()
            .onEach {
                recent.clear()
                recent.addAll(it.reversed())
            }
            .launchIn(scope)

        runBlocking { db.nameInfoDao().getAll().firstOrNull()?.lastOrNull()?.let { ifyInfo = it } }*/
        scope.launch {
            db.list()
                .onEach {
                    recent.clear()
                    recent.addAll(it)
                    if (start) {
                        recent.lastOrNull()?.let { i -> ifyInfo = i }
                        start = false
                    }
                }
                .launchIn(scope)
        }
    }

    fun getInfo() {
        scope.launch {
            state = NetworkState.Loading
            val n = name.trim()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            recent.find { it.name == n }?.let { ifyInfo = it } ?: run {
                service.getInfo(n, "us").fold(
                    onSuccess = {
                        ifyInfo = it
                        scope.launch { db.saveIfy(it) }
                    },
                    onFailure = { it.printStackTrace() }
                )
            }
            state = NetworkState.NotLoading
        }
    }

    fun onRecentPress(info: IfyInfo) {
        ifyInfo = info
    }

    fun onRemovePress(info: IfyInfo) {
        scope.launch { db.removeIfy(info) }
    }

}

internal enum class NetworkState { Loading, NotLoading }

internal val MaleColor = Color(0xff448aff)
internal val FemaleColor = Color(0xfff06292)

internal sealed class ImageLoading {
    object Loading : ImageLoading()
    data class Loaded(val image: ImageBitmap) : ImageLoading()
}