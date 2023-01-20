package com.programmersbox.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.programmersbox.database.IfyDatabase
import com.programmersbox.info.NameInfoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        NameInfoDatabase.Schema.create(driver)
        return driver
    }
}

internal actual class IfyInfoDatabase actual constructor(scope: CoroutineScope) {
    private val realm = IfyDatabase()
    private val json = Json {
        isLenient = true
    }

    actual suspend fun saveIfy(ifyInfo: IfyInfo) {
        realm.saveInfo(
            ifyInfo.name,
            ifyInfo.age,
            json.encodeToString(ifyInfo.gender),
            json.encodeToString(ifyInfo.nationality)
        )
    }

    actual suspend fun removeIfy(ifyInfo: IfyInfo) {
        realm.removeInfo(
            ifyInfo.name,
            ifyInfo.age,
            json.encodeToString(ifyInfo.gender),
            json.encodeToString(ifyInfo.nationality)
        )
    }

    actual suspend fun list(): Flow<List<IfyInfo>> = realm.getItems()
        .map { list ->
            list.map {
                IfyInfo(
                    name = it.name,
                    age = it.age,
                    gender = json.decodeFromString(it.gender),
                    nationality = json.decodeFromString(it.nationality)
                )
            }
        }
}