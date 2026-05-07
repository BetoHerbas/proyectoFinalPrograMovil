import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)

    id("io.sentry.android.gradle") version "6.1.0"
    alias(libs.plugins.google.gms.google.services)
}

// Sentry Android Gradle plugin injects its Android-only artifacts into ALL Gradle
// configurations, including KMP iOS ones. Explicitly exclude them from every
// configuration whose name indicates an iOS / Kotlin-Native target.
configurations.configureEach {
    if (name.contains("ios", ignoreCase = true) ||
        name.startsWith("native") ||
        name.contains("Native")
    ) {
        exclude(group = "io.sentry")
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.androidx.room.sqlite.wrapper)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.auth)
            implementation(libs.firebase.config)
            implementation(libs.firebase.database)
            implementation(libs.kotlinx.coroutines.play.services)
            implementation(libs.ktor.client.okhttp)
            // Sentry scoped to Android only (plugin auto-install is disabled for KMP compatibility)
            implementation("io.sentry:sentry-android:8.33.0")
            implementation("io.sentry:sentry-compose-android:8.33.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(project(":designsystem"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.ui.test.junit4)
        }
        
    }
}

android {
    namespace = "com.ucb.proyectofinal"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ucb.proyectofinal"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    debugImplementation(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    // Add any other platform target you use in your project, for example kspDesktop

}

room {
    schemaDirectory("$projectDir/schemas")
}

sentry {
    org.set("universidad-katolica-bolivi-4y")
    projectName.set("ucb")

    includeSourceContext.set(true)

    // Prevent the plugin from auto-adding sentry-android artifacts to all
    // Gradle configurations (including iOS KMP targets, which are Android-only).
    autoInstallation {
        enabled.set(false)
    }
    tracingInstrumentation {
        enabled.set(false)
    }
}

// Workaround: Sentry tasks need to depend on Compose resource generation tasks
tasks.configureEach {
    if (name.startsWith("generateSentryBundleId")) {
        tasks.findByName("generateResourceAccessorsForAndroidMain")?.let {
            dependsOn(it)
        }
        tasks.findByName("generateActualResourceCollectorsForAndroidMain")?.let {
            dependsOn(it)
        }
    }
}

tasks.register<LocoSyncTask>("syncTranslations") {
    apiKey = EnvLoader.get("LOCO_API_KEY") ?: "TU_API_KEY_AQUI"
}
