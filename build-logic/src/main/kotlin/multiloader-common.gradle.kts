plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of("java_version"())
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain("java_version"().toInt())
}

repositories {}

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${"mod_name"()}" }
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Specification-Title" to "mod_name"(),
            "Specification-Vendor" to "mod_authors"(),
            "Specification-Version" to project.version,
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "mod_authors"(),
            "Built-On-Minecraft" to "minecraft_version"()
        )
    }
}

tasks.named<ProcessResources>("processResources") {
    val expandProps = mapOf(
        "version" to project.version,
        "group" to project.group, //Else we target the task's group.
        "minecraft_version" to "minecraft_version"(),
        "minecraft_version_range" to "minecraft_version_range"(),
        "fabric_loader_version" to "fabric_loader_version"(),
        "fabric_api_version" to "fabric_api_version"(),
        "neoforge_version" to "neoforge_version"(),
        "mod_name" to "mod_name"(),
        "mod_authors" to "mod_authors"(),
        "mod_id" to "mod_id"(),
        "license" to "license"(),
        "description" to project.description,
        "credits" to "credits"(),
        "java_version" to "java_version"(),
        "issue_tracker" to "issue_tracker"(),
        "homepage" to "homepage"(),
        "logo" to "logo"(),
        "update_json_url" to "update_json_url"()
    )

    val jsonExpandProps = expandProps.mapValues { (_, value) ->
        if (value is String) value.replace("\n", "\\\\n") else value
    }

    filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand(expandProps)
    }

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
        expand(jsonExpandProps)
    }

    inputs.properties(expandProps)
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        val localMavenUrl = System.getenv("local_maven_url")
        if (localMavenUrl != null) {
            maven {
                url = uri(localMavenUrl)
            }
        }
    }
}

// THANK YOU IThundxr I love you
operator fun String.invoke(): String {
    return providers.gradleProperty(this).orNull ?: throw IllegalStateException("Property $this is not defined")
}