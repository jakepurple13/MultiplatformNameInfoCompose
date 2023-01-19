package com.programmersbox.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.sqljs.initSqlDriver
import com.programmersbox.info.NameInfoDatabase
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asDeferred

internal actual class DriverFactory {
    @OptIn(ExperimentalCoroutinesApi::class)
    actual fun createDriver(): SqlDriver {
        return initSqlDriver(NameInfoDatabase.Schema).asDeferred().getCompleted()
    }
}