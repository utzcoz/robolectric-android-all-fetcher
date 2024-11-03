import java.security.MessageDigest

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    `java-library`
    id("com.diffplug.spotless") version "7.0.0.BETA4"
}

spotless {
    kotlinGradle {
        target("*.gradle.kts") // default target for kotlinGradle
        ktlint()
    }
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    google()
    gradlePluginPortal()
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// Copy from Robolectric's AndroidSdk.kt.
// When there are some changes for one Robolectric's version, these changes should
// be synced here.
// This version is from 4.14-beta-1
private val preInstrumentedVersion = 7
private val androidAllJars =
    listOf(
        Pair("5.0.2_r3", "r0"),
        Pair("5.1.1_r9", "r2"),
        Pair("6.0.1_r3", "r1"),
        Pair("7.0.0_r1", "r1"),
        Pair("8.0.0_r4", "r1"),
        Pair("8.1.0", "4611349"),
        Pair("9", "4913185-2"),
        Pair("10", "5803371"),
        Pair("11", "6757853"),
        Pair("12", "7732740"),
        Pair("12.1", "8229987"),
        Pair("13", "9030017"),
        Pair("14", "10818077"),
        Pair("15", "12543294"),
    )

/**
 * Calculates sha256 string for a maven dependency.
 *
 * It uses stream to update digest calculation one by one to avoid large memory usage when
 * dependency file is very large.
 */
fun calculateFileSha256(artifactFile: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    artifactFile.inputStream().use { inputStream ->
        val buffer = ByteArray(1024 * 1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
    }
    val hashBytes = digest.digest()
    val hashString = hashBytes.joinToString("") { String.format("%02x", it.toInt() and 0xFF) }
    println("SHA-256 of ${artifactFile.name}: $hashString")
    return hashString
}

tasks.register("updateAndroidAllJarsMetadata") {
    androidAllJars.forEach { version ->
        val androidAllConfiguration = configurations.create("androidAllConfiguration${version.first}")
        val preinstrumentedAndroidAllConfiguration = configurations.create("preinstrumentedAndroidAllConfiguration${version.first}")

        dependencies {
            println("Configure android-all $version")
            androidAllConfiguration(
                "org.robolectric:android-all:" +
                    "${version.first}-robolectric-${version.second}",
            )
            println("Configure android-all-instrumented $version")
            preinstrumentedAndroidAllConfiguration(
                "org.robolectric:android-all-instrumented:" +
                    "${version.first}-robolectric-${version.second}-i$preInstrumentedVersion",
            )
        }

        androidAllConfiguration.resolve().forEach { artifactFile ->
            calculateFileSha256(artifactFile)
        }

        preinstrumentedAndroidAllConfiguration.resolve().forEach { artifactFile ->
            calculateFileSha256(artifactFile)
        }
    }
}
