package com.programmersbox.shared

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.programmersbox.info.NameInfoDatabase

internal actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(NameInfoDatabase.Schema, context, "test.db")
    }
}