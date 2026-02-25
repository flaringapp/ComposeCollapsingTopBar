plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.compose.multiplatform)
}

kotlin {
    jvmToolchain(17)

    android {
        namespace = "com.flaringapp.compose.topbar.sample.shared"
        compileSdk = 36

        androidResources {
            enable = true
        }
    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "SampleShared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":ComposeCollapsingTopBar"))

            implementation(libs.compose.multiplatform.ui)
            implementation(libs.compose.multiplatform.uiToolingPreview)
            implementation(libs.compose.multiplatform.components.resources)
            implementation(libs.compose.multiplatform.material3)
            implementation(libs.compose.multiplatform.materialIconsCore)
            implementation(libs.compose.multiplatform.navigationEvent)
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_reports")
}

dependencies {
    add("androidRuntimeClasspath", libs.compose.multiplatform.uiTooling)
    add("lintChecks", libs.slack.compose.linter)
}
