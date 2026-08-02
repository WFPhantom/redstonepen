plugins {
    id("multiloader-common")
    id("net.neoforged.moddev")
}

val neo_form_version = project.property("neo_form_version") as String

neoForge {
    neoFormVersion = neo_form_version
    // Automatically enable AccessTransformers if the file exists
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) accessTransformers.from(at.absolutePath)
    parchment{
        minecraftVersion = "1.21.1"
        mappingsVersion = "2024.11.17"
    }
}

dependencies {
    // Fabric and NeoForge both bundle Fabric Mixin, so it is safe to use it in common
    // If you need to update, check what version they are using to see what is compatible
    // https://github.com/neoforged/NeoForge/blob/26.2.x/gradle.properties#L37
    // https://github.com/FabricMC/fabric-loader/blob/master/gradle.properties#L12
    compileOnly("net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7")
    // Fabric and NeoForge both bundle MixinExtras, so it is safe to use it in common
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")!!)
}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    sourceSets.main.get().kotlin.sourceDirectories.forEach { dir -> add("commonJava", dir) }
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}

// Implement mcgradleconventions loader attribute
val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations.named(variant) {
        attributes {
            attribute(loaderAttribute, "common")
        }
    }
}

sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "common")
            }
        }
    }
}