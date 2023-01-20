package com.programmersbox.shared

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.programmersbox.info.NameInfoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        return NativeSqliteDriver(NameInfoDatabase.Schema, "test.db")
    }
}

internal actual class IfyInfoDatabase actual constructor(private val scope: CoroutineScope) {

    private val db by lazy { scope.async { createDatabase(DriverFactory()).nameInfoQueries } }

    actual suspend fun saveIfy(ifyInfo: IfyInfo) {
        //listInfo.update { it.apply { add(ifyInfo) } }
        db.await().addInfo(ifyInfo)
    }

    actual suspend fun removeIfy(ifyInfo: IfyInfo) {
        //listInfo.update { it.apply { remove(ifyInfo) } }
        db.await().deleteInfo(ifyInfo.name)
    }

    actual suspend fun list(): Flow<List<IfyInfo>> = db.await()
        .getInfo().asFlow()
        .mapToList(scope.coroutineContext)
        .map { it.mapToIfy() }
}

/*
internal actual class IfyInfoDatabase {
    private val realm = Database()
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
                    nationality = json.decodeFromString(it.gender)
                )
            }
        }
}*/
