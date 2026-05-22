import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.compose.multiplatform)
}

dependencies {
    implementation(project(":sample:shared"))

    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "com.flaringapp.compose.topbar.sample.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.flaringapp.compose.topbar.sample.desktop"
            packageVersion = "1.0.0"
        }
    }
}
