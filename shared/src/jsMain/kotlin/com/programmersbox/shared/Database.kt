package com.programmersbox.shared

import com.programmersbox.info.NameInfoDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.sqljs.initSqlDriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asDeferred

internal actual class DriverFactory {
    @OptIn(ExperimentalCoroutinesApi::class)
    actual fun createDriver(): SqlDriver {
        return initSqlDriver(NameInfoDatabase.Schema).asDeferred().getCompleted()
    }
}