pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven {
                    name = "Fabric"
                    url = uri("https://maven.fabricmc.net/")
                }
            }
            filter {
                includeGroupAndSubgroups("net.fabricmc")
            }
        }
    }
    plugins{
        id("net.fabricmc.fabric-loom-remap") version providers.gradleProperty("loom_version")
        id("net.neoforged.moddev") version providers.gradleProperty("mdg_version")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val modId = providers.gradleProperty("mod_id").get()

includeBuild("build-logic")
include("$modId-common")
project(":$modId-common").projectDir = file("common")

include("$modId-fabric")
project(":$modId-fabric").projectDir = file("fabric")

include("$modId-neoforge")
project(":$modId-neoforge").projectDir = file("neoforge")