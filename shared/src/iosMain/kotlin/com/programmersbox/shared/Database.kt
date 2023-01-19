package com.programmersbox.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.programmersbox.info.NameInfoDatabase

internal actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(NameInfoDatabase.Schema, "test.db")
    }
}