import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.ir.backend.js.compile
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    kotlin("kapt")
}

android {
    signingConfigs {
        create("release") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            val config = getSigningConfig()
            val keyPath = config["storeFile"] ?: System.getenv("KEYSTORE_PATH")
            storeFile = file(keyPath)
            storePassword = config.getProperty("storePassword") ?: System.getenv("KEYSTORE_PWD")
            keyAlias = config.getProperty("keyAlias") ?: System.getenv("KEY_ALIAS")
            keyPassword = config.getProperty("keyPassword") ?: System.getenv("KEY_PWD")
        }
    }

    namespace = "com.houvven.guise"
    compileSdk = 33

    defaultConfig {
        applicationId = namespace
        minSdk = 29
        targetSdk = compileSdk
        val versionConfig = getVersionConfig()
        versionCode = versionConfig["versionCode"].toString().toInt()
        versionName = versionConfig["versionName"].toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        signingConfig = signingConfigs.getByName("release")

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/schemas".toString())
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/**"
            excludes += "/kotlin/**"
            excludes += "/*.txt"
            excludes += "/*.bin"
        }
        dex {
            useLegacyPackaging = true
        }
    }
    applicationVariants.all {
        outputs.all {
            this as BaseVariantOutputImpl
            this.outputFileName = "${rootProject.name}-${versionName}.${name}.apk"
        }
    }
}

tasks.register("updateVersion") {
    doLast {
        val versionConfig = Properties()
        val file = rootProject.file("./app/version.properties")
        file.inputStream().use { versionConfig.load(it) }

        val versionCode = versionConfig["versionCode"].toString().toInt()
        versionConfig["versionCode"] = (versionCode + 1).toString()
        val versionName = versionConfig["versionName"].toString()
        val versionNameSplit = versionName.split(".")
        val (major, minor, patch) = versionNameSplit
        versionConfig["versionName"] = when {
            patch.toInt() < 9 -> "$major.$minor.${patch.toInt() + 1}"
            minor.toInt() < 9 -> "$major.${minor.toInt() + 1}.0"
            else -> "${major.toInt() + 1}.0.0"
        }
        file.outputStream().use { versionConfig.store(it, null) }
        println("Version updated to ${versionConfig["versionName"]}")
    }
}

dependencies {

    implementation(project(mapOf("path" to ":lib")))
    val composeVersion = "1.3.2"
    val accompanistVersion = "0.28.0"
    val roomVersion = "2.5.0"


    compileOnly("de.robv.android.xposed:api:82")
    implementation(project(":ktx-xposed"))
    implementation("com.tencent:mmkv:1.2.15")

    // Kotlin-serilization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    // Activity Result API
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")

    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")


    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")

    androidTestImplementation(platform("androidx.compose:compose-bom:2022.10.00"))
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")

    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets-ui:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}

fun getSigningConfig(): Properties {
    val properties = Properties()
    runCatching { rootProject.file("local.properties").inputStream().use { properties.load(it) } }
    return properties
}

fun getVersionConfig(): Map<*, *> {
    val versionConfig = Properties()
    rootProject.file("app/version.properties").inputStream().use {
        versionConfig.load(it)
        return versionConfig.toMap()
    }
}