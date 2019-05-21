group = "org.binspec"
version = "0.0.1-SNAPSHOT"

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Libs.Kotlin.stdLib)
    implementation(Libs.Kotlin.coroutinesCore)
    implementation(Libs.Kotlin.coroutinesJdk8)
    implementation(kotlin("reflect"))
    testImplementation(Libs.JUnit.jupiterAPI)
    testImplementation(Libs.JUnit.jupiterParameterized)
    testRuntime(Libs.JUnit.jupiterEngine)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes"
        kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        kotlinOptions.freeCompilerArgs += "-XXLanguage:+InlineClasses"
        kotlinOptions.freeCompilerArgs += "-Xallow-result-return-type"
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes"
        kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        kotlinOptions.freeCompilerArgs += "-XXLanguage:+InlineClasses"
        kotlinOptions.freeCompilerArgs += "-Xallow-result-return-type"
        kotlinOptions.jvmTarget = "1.8"
    }
}