plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev")
    idea
}

neoForge {
    parchment{
        minecraftVersion = "1.21.1"
        mappingsVersion = "2024.11.17"
    }
    version = "neoforge_version"()
    // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":${"mod_id"()}-common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) accessTransformers.from(at.absolutePath)

    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", "mod_id"())
        }
        create("client") {
            client()
        }
        create("server") {
            server()
            programArgument("--nogui")
        }
        create("gameTestServer") {
            type = "gameTestServer"
        }
        create("data") {
            data()
            // DataGen can be run by - "./gradlew :neoforge:runData" in Terminal.
            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll("--mod", "mod_id"(), "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
        }
    }

    mods {
        create("mod_id"()) {
            sourceSet(sourceSets.main.get())
        }
    }
}
repositories {
    maven { url = uri("https://api.modrinth.com/maven") }
}

dependencies {
    compileOnly("maven.modrinth:jade:${"jade_version"()}")
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")

    exclude("**/*.bbmodel") // BlockBench project files
    exclude("src/generated/**/.cache") // datagen cache files
}

// Implement mcgradleconventions loader attribute
val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations.named(variant) {
        attributes {
            attribute(loaderAttribute, "neoforge")
        }
    }
}

sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName, getTaskName(null, "jarJar")).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "neoforge")
            }
        }
    }
}

// Sets up a dependency configuration called 'localRuntime'.
// This configuration should be used instead of 'runtimeOnly' to declare
// a dependency that will be present for runtime testing but that is
// "optional", meaning it will not be pulled by dependents of this mod.
configurations {
    val localRuntime = create("localRuntime")
    named("runtimeClasspath") {
        extendsFrom(localRuntime)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

operator fun String.invoke(): String {
    return providers.gradleProperty(this).orNull ?: throw IllegalStateException("Property $this is not defined")
}