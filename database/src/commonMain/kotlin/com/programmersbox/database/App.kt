package com.programmersbox.database

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.migration.AutomaticSchemaMigration
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

internal class IfySavedInfo : RealmObject {
    var ifyList: RealmList<IfyItem> = realmListOf<IfyItem>()
}

public class IfyItem() : RealmObject {
    @field:PrimaryKey
    public var name: String = ""
    public var age: Int = 0
    public var gender: String = "{\"gender\":\"male\",\"probability\":50.0}"
    public var nationality: String = "[]"

    public constructor(name: String, age: Int, gender: String, nationality: String) : this() {
        this.name = name
        this.age = age
        this.gender = gender
        this.nationality = nationality
    }
}

public class IfyDatabase {
    private val realm by lazy {
        Realm.open(
            RealmConfiguration.Builder(setOf(IfySavedInfo::class, IfyItem::class))
                .schemaVersion(4)
                .migration(AutomaticSchemaMigration { })
                .deleteRealmIfMigrationNeeded()
                .build()
        )
    }

    private suspend fun initialDb(): IfySavedInfo {
        val f = realm.query(IfySavedInfo::class).first().find()
        return f ?: realm.write { copyToRealm(IfySavedInfo()) }
    }

    public suspend fun getItems(): Flow<List<IfyItem>> = initialDb().asFlow()
        .mapNotNull { it.obj }
        .distinctUntilChanged()
        .map { it.ifyList.toList() }

    public suspend fun saveInfo(name: String, age: Int, gender: String, nationality: String) {
        realm.updateInfo<IfySavedInfo> {
            it?.ifyList?.add(IfyItem(name, age, gender, nationality))
        }
    }

    public suspend fun removeInfo(name: String, age: Int, gender: String, nationality: String) {
        realm.updateInfo<IfySavedInfo> {
            it?.ifyList?.remove(IfyItem(name, age, gender, nationality))
        }
    }
}

private suspend inline fun <reified T : RealmObject> Realm.updateInfo(crossinline block: MutableRealm.(T?) -> Unit) {
    query(T::class).first().find()?.also { info ->
        write { block(findLatest(info)) }
    }
}