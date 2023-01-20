package com.programmersbox.shared

import androidx.compose.runtime.Composable

public actual fun getPlatformName(): String {
    return "JavaScript"
}

@Composable
public fun UIShow() {
    App(true)
}