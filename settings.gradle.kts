pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    //
    plugins {
        //    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"

            kotlin("jvm").version(extra["kotlin.version"] as String)
            id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}
rootProject.name = "arctest"

