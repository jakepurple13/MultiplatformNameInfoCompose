package com.programmersbox.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.sqljs.initSqlDriver
import com.programmersbox.info.NameInfoDatabase
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.browser.localStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//TODO: Maybe make a new module for desktop, android, and ios to use realm and use something else for js
internal actual class DriverFactory {
    @OptIn(ExperimentalCoroutinesApi::class)
    actual suspend fun createDriver(): SqlDriver {
        return initSqlDriver(NameInfoDatabase.Schema).await()
    }
}

internal actual class IfyInfoDatabase actual constructor(scope: CoroutineScope) {
    private val listInfo = MutableStateFlow<IfyItemList>(IfyItemList())
    //private val listInfo = Channel<IfyItemList>(Channel.UNLIMITED)
    //private val listInfo = MutableSharedFlow<MutableList<IfyInfo>>()

    private val json = Json {
        isLenient = true
        prettyPrint = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    init {
        scope.launch {
            localStorage.getItem("IfyItems")
                ?.let { json.decodeFromString<List<IfyInfo>>(it).toMutableList() }
                //?.let { listInfo.update { it.toMutableList() } }
                ?.let { listInfo.value = IfyItemList(it) }
        }

        listInfo
            .map { it.list }
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .onEach { localStorage.setItem("IfyItems", json.encodeToString(it)) }
            .launchIn(scope)
    }

    actual suspend fun saveIfy(ifyInfo: IfyInfo) {
        listInfo.value = listInfo.value
            .let { it.copy(list = it.list.toMutableList().apply { add(ifyInfo) }.toList()) }
    }

    actual suspend fun removeIfy(ifyInfo: IfyInfo) {
        listInfo.value = listInfo.value
            .let { it.copy(list = it.list.toMutableList().apply { remove(ifyInfo) }.toList()) }
    }

    actual suspend fun list(): Flow<List<IfyInfo>> = listInfo.map { it.list }
}

private data class IfyItemList(val list: List<IfyInfo> = emptyList())