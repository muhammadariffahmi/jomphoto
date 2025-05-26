// This is for Kotlin DSL (settings.gradle.kts)

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "JomPhoto"

// Define the path to OpenCV SDK using Kotlin syntax
val opencvsdk = File(rootDir, "opencv")

include(":app")
include(":opencv")
project(":opencv").projectDir = File(opencvsdk, "sdk")
