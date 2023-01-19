package com.programmersbox.shared

import com.programmersbox.info.NameInfoDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

internal actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(NameInfoDatabase.Schema, "test.db")
    }
}