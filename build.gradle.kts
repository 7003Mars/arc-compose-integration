plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.compose")
    application
}

group = "me.mars"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository")
    maven("https://maven.xpdustry.com/anuken")
    maven("https://www.jitpack.io")
}

dependencies {
    api("com.github.Anuken.Arc:arc-core:v146")
    implementation("com.github.Anuken.Arc:natives-desktop:v146")
    implementation("com.github.Anuken.Arc:backend-sdl:v146")
    implementation(compose.desktop.currentOs.also(::println))

    implementation("org.jetbrains.skiko:skiko:0.7.90")
    implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.90")

    implementation("io.github.humbleui:skija-shared:0.116.2")
    implementation("io.github.humbleui:skija-windows-x64:0.116.2")
}


configurations.all {
    resolutionStrategy.eachDependency {
        if (this.requested.group == "org.jetbrains.skiko") {
            this.useVersion("0.7.90")
        }
    }
}


application {
    mainClass.set("me.mars.MainKt")
}
//kotlin {
//    jvmToolchain(21)
//}