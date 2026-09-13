pluginManagement {
    repositories {
        google()
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

rootProject.name = "SafeTunnel"

include(":app")
include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:navigation")
include(":feature:auth")
include(":feature:home")
include(":feature:servers")
include(":feature:settings")