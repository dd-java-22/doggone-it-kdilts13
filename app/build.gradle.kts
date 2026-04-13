/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.schema.parser)
    alias(libs.plugins.junit)
    alias(libs.plugins.kotlin.android)
}

android {

    namespace = project.property("basePackageName") as String?
    compileSdk = (project.property("targetSdk") as String).toInt()

    defaultConfig {

        applicationId = project.property("basePackageName") as String
        minSdk = (project.property("minSdk") as String).toInt()
        targetSdk = (project.property("targetSdk") as String).toInt()
        versionCode = (project.property("versionCode") as String).toInt()
        versionName = project.property("version") as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"

        resValue("string", "app_name", project.property("appName") as String)
        resValue("string", "client_id", getLocalProperty("clientId") as String)
        buildConfigField("String", "DOG_API_KEY", asJavaString(getLocalPropertyOrDefault("dogApiKey", "")))

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true",
                        "room.expandProjection" to "true"
                    )
                )
            }
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.java.get()}")
        targetCompatibility = JavaVersion.valueOf("VERSION_${libs.versions.java.get()}")
    }

    kotlin {
        jvmToolchain(libs.versions.java.get().toInt())
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        // Enable dataBinding if desired.
        // dataBinding = true
    }

    androidResources {
        noCompress += "tflite"
    }

}

dependencies {

    // dependencies for tensorflow for image recognition
    implementation("com.google.ai.edge.litert:litert:2.1.0")
    // NOTE: `litert-support` removed for now due to duplicate classes with `litert`.

    // kotlin standard library and coroutines
    implementation(libs.kotlin)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.jdk8)
    implementation(libs.kotlin.coroutines.android)

    // Desugaring for subset of JDK
    coreLibraryDesugaring(libs.desugar)

    // Basic Android components
    implementation(libs.app.compat)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.constraint.layout)
    implementation(libs.recycler.view)

    // Navigation framework libraries
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Lifecycle (LiveData and ViewModel) libraries
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Preferences/settings components
    implementation(libs.preference)

    // Material Design components
    implementation(libs.material)

    // Room annotation processor, runtime library, and ReactiveX integration
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Gson (Google JSON parser) library
    implementation(libs.gson)

    // Glide for efficient local gallery thumbnail loading.
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Google Sign-in library
    implementation(libs.play.auth)

    // credential manager libraries
    implementation(libs.credentials)
    implementation(libs.credentials.play.services)
    implementation(libs.googleid)

    // Retrofit (REST client) with Gson integration
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)

    // Hilt dependency-injection library & annotation processor
    implementation(libs.hilt.android.core)
    annotationProcessor(libs.hilt.compiler)

    // OkHttp logging dependency
    implementation(libs.logging.interceptor)

    // Libraries for JVM-based testing.
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testRuntimeOnly(libs.junit.engine)

    // Libraries for instrumented (run in Android) testing.
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.junit.android.core)
    androidTestRuntimeOnly(libs.junit.android.runner)
    androidTestImplementation(libs.junit.api)
    androidTestImplementation(libs.junit.params)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestAnnotationProcessor(libs.hilt.compiler)
    androidTestAnnotationProcessor(libs.hilt.android.compiler)

    constraints {
        implementation(libs.kotlin.jdk7) {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        //noinspection ForeignDelegate
        implementation(libs.kotlin.jdk8) {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }
}

roomDdl {
    source.set(project.file("$projectDir/schemas/edu.cnm.deepdive.doggoneit.service.DoggoneItDatabase/1.json"))
    destination.set(project.file("$projectDir/../docs/sql/ddl.sql"))
}

// Standard Javadoc for app main Java sources (milestone documentation output).
tasks.register<Javadoc>("javadoc") {
    group = "documentation"
    description = "Generates Javadoc HTML for the app module main Java sources."

    val mainSourceSet = android.sourceSets.getByName("main")
    setSource(mainSourceSet.java.srcDirs)
    include("**/*.java")
    exclude(
        "**/R.java",
        "**/BuildConfig.java",
        "**/*Directions.java",
        "**/*Args.java",
        "**/databinding/**",
        "**/Hilt_*",
        "**/*_Factory.java",
        "**/*_MembersInjector.java",
        "**/*_GeneratedInjector.java",
        "**/Dagger*",
        "**/hilt_aggregated_deps/**",
        "**/dagger/hilt/internal/**"
    )

    setDestinationDir(file("$rootDir/docs/api"))

    (options as StandardJavadocDocletOptions).apply {
        isAuthor = false

        links(
            "https://docs.oracle.com/en/java/javase/21/docs/api/",
            "https://javadoc.io/doc/com.google.dagger/dagger/latest/",
            "https://javadoc.io/doc/com.google.dagger/hilt-android/latest/",
            "https://javadoc.io/doc/com.github.bumptech.glide/glide/4.16.0/"
        )

        linksOffline("https://developer.android.com/reference", "$projectDir")

        addBooleanOption("html5", true)
        addStringOption("Xdoclint:none", "-quiet")
        encoding = "UTF-8"
        charSet = "UTF-8"
    }
}

android.applicationVariants.configureEach {
    if (name == "debug") {
        tasks.named<Javadoc>("javadoc").configure {
            // Use debug Java compile inputs so Android/third-party symbols resolve during Javadoc.
            dependsOn(javaCompileProvider)
            setSource(javaCompileProvider.get().source)
            classpath = files(
                android.bootClasspath,
                javaCompileProvider.get().classpath,
                layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")
            )
        }
    }
}

fun getLocalProperty(name: String): String {
    return getProperty("$projectDir/local.properties", name)
}

fun getLocalPropertyOrDefault(name: String, defaultValue: String): String {
    return getPropertyOrDefault("$projectDir/local.properties", name, defaultValue)
}

fun getProperty(filename: String, name: String): String {
    return FileInputStream(filename).use {
        val props = Properties()
        props.load(it)
        props.getProperty(name)
    }
}

fun getPropertyOrDefault(filename: String, name: String, defaultValue: String): String {
    return FileInputStream(filename).use {
        val props = Properties()
        props.load(it)
        props.getProperty(name, defaultValue)
    }
}

fun asJavaString(value: String): String {
    return "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""
}
