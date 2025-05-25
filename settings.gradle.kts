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
include(":app")
include(":opencv")
//include(":opencv:sdk")
//include(":opencv:sdk")
//include(":opencv:sdk")
project(":opencv").projectDir = file("opencv/OpenCV-android-sdk/sdk")
