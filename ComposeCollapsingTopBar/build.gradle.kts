import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.compose.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    jvmToolchain(17)

    explicitApi()

    android {
        namespace = "com.flaringapp.compose.topbar"
        compileSdk = 36
        minSdk = 23

        @Suppress("UnstableApiUsage")
        optimization {
            minify = false
            consumerKeepRules.apply {
                publish = true
                file("consumer-rules.pro")
            }
        }

        aarMetadata {
            minCompileSdk = minSdk
        }
    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeCollapsingTopBar"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.multiplatform.foundation)
            implementation(libs.compose.multiplatform.ui)
        }
    }

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation {
        enabled.set(true)
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_reports")
}

dependencies {
    add("lintChecks", libs.slack.compose.linter)
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            androidVariantsToPublish = listOf("release"),
            javadocJar = JavadocJar.Dokka("dokkaGenerate"),
            sourcesJar = SourcesJar.Sources(),
        ),
    )
}
