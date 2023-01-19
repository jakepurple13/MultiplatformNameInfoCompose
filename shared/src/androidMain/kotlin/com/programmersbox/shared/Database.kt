package com.programmersbox.shared

import android.content.Context
import com.programmersbox.info.NameInfoDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

internal actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(NameInfoDatabase.Schema, context, "test.db")
    }
}