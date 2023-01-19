package com.programmersbox.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

public actual fun getPlatformName(): String {
    return "JavaScript"
}

@Composable
public fun UIShow() {
    App(remember { DriverFactory() })
}