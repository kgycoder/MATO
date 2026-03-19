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
        // NewPipeExtractor 는 JitPack 에서 배포
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "XWare"
include(":app")
