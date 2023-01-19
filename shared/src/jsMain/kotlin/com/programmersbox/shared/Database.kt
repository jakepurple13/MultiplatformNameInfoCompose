package com.programmersbox.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.sqljs.initSqlDriver
import com.programmersbox.info.NameInfoDatabase
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.await

internal actual class DriverFactory {
    @OptIn(ExperimentalCoroutinesApi::class)
    actual suspend fun createDriver(): SqlDriver {
        return initSqlDriver(NameInfoDatabase.Schema).await()
    }
}