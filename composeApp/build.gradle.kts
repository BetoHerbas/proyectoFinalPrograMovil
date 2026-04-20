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
            implementation(libs.firebase.config)
            implementation(libs.firebase.database)
            implementation(libs.kotlinx.coroutines.play.services)
        }
        commonMain.dependencies {
            implementation(project(":designsystem"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
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
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
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
    org.set("universidad-catolica-bolivi-4y")
    projectName.set("ucb")

    // this will upload your source code to Sentry to show it as part of the stack traces
    // disable if you don't want to expose your sources
    includeSourceContext.set(true)
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
