package com.programmersbox.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.sqljs.initSqlDriver
import com.programmersbox.info.NameInfoDatabase
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.*

//TODO: Maybe make a new module for desktop, android, and ios to use realm and use something else for js
internal actual class DriverFactory {
    @OptIn(ExperimentalCoroutinesApi::class)
    actual suspend fun createDriver(): SqlDriver {
        return initSqlDriver(NameInfoDatabase.Schema).await()
    }
}

internal actual class IfyInfoDatabase actual constructor(scope: CoroutineScope) {
    private val listInfo = MutableStateFlow<MutableList<IfyInfo>>(mutableListOf())

    actual suspend fun saveIfy(ifyInfo: IfyInfo) {
        listInfo.update { it.apply { add(ifyInfo) } }
    }

    actual suspend fun removeIfy(ifyInfo: IfyInfo) {
        listInfo.update { it.apply { remove(ifyInfo) } }
    }

    actual suspend fun list(): Flow<List<IfyInfo>> = listInfo
}