object Libs {
    object Kotlin {
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val coroutinesJdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.kotlin_coroutines}"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlin_coroutines}"

    }
    object JUnit {
        const val jupiterAPI = "org.junit.jupiter:junit-jupiter-api:${Versions.junit}"
        const val jupiterParameterized = "org.junit.jupiter:junit-jupiter-params:${Versions.junit}"
        const val jupiterEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
    }
}