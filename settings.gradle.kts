pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
val properties = java.util.Properties()
file("local.properties").inputStream().use { properties.load(it) }

// add to local.properties mapboxPrivateKey= ""
val mapboxPrivateKey: String? = properties.getProperty("mapboxPrivateKey")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")

            credentials {
                username = "mapbox"
//                password = mapboxPrivateKey
                password = "sk.eyJ1IjoibWF0eW1hc2FyeWsiLCJhIjoiY2xwa2FveWhyMDlzbjJpa2VoZDQ1cG96aCJ9.X1_YVHdSH0dwUl_Y4xn46A"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "MOBV Zadanie"
include(":app")
