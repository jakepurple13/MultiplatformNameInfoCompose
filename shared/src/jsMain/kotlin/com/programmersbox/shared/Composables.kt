package com.programmersbox.shared

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jetbrains.skia.Image

@Composable
internal fun loadImage(url: String): State<ImageLoading> = produceState<ImageLoading>(ImageLoading.Loading) {
    try {
        value = ImageLoading.Loaded(
            Image.makeFromEncoded(HttpClient().get(url).readBytes()).toComposeImageBitmap()
        )
    } catch (e: Exception) {
        println(url)
        e.printStackTrace()
    }
}

@Composable
internal actual fun NetworkImage(url: String, modifier: Modifier) {
    val loading = loadImage(url).value

    if (loading is ImageLoading.Loaded) {
        Image(
            bitmap = loading.image,
            contentDescription = null,
            modifier = modifier
        )
    }
}