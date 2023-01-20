group = "com.programmersbox"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
    id("org.jetbrains.kotlin.plugin.serialization") apply false
    id("app.cash.sqldelight") apply false
}

buildscript {
    dependencies {
        classpath("io.realm.kotlin:gradle-plugin:1.5.2")
        classpath("app.cash.sqldelight:gradle-plugin:2.0.0-alpha04")
    }
}