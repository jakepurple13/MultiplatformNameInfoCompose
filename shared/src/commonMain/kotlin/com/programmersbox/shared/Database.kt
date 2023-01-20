package com.programmersbox.shared

import app.cash.sqldelight.db.SqlDriver
import com.programmersbox.info.NameInfoDatabase
import com.programmersbox.info.NameInfoItem
import com.programmersbox.info.NameInfoQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal expect class DriverFactory {
    suspend fun createDriver(): SqlDriver
}

internal suspend fun createDatabase(driverFactory: DriverFactory): NameInfoDatabase {
    val driver = driverFactory.createDriver()
    val database = NameInfoDatabase(driver)
    // Do more work with the database (see below).

    return database
}

internal expect class IfyInfoDatabase(scope: CoroutineScope) {
    suspend fun list(): Flow<List<IfyInfo>>
    suspend fun saveIfy(ifyInfo: IfyInfo)
    suspend fun removeIfy(ifyInfo: IfyInfo)
}

internal fun NameInfoQueries.addInfo(info: IfyInfo) {
    addInfo(
        name = info.name,
        age = info.age.toLong(),
        gender = Json.encodeToString(info.gender),
        nationality = Json.encodeToString(info.nationality),
    )
}

internal fun List<NameInfoItem>.mapToIfy() = map {
    IfyInfo(
        name = it.name,
        age = it.age.toInt(),
        gender = Json.decodeFromString(it.gender),
        nationality = Json.decodeFromString(it.nationality)
    )
}