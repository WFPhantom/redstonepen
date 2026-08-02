plugins {
    id("multiloader-common")
}

val commonPath = ":${providers.gradleProperty("mod_id").get()}-common"

configurations {
    create("commonJava") {
        isCanBeResolved = true
    }
    create("commonResources") {
        isCanBeResolved = true
    }
}

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)

dependencies {
    compileOnly(project(commonPath)) {
        attributes {
            attribute(loaderAttribute, "common")
        }
    }
    add("commonJava", project(path = commonPath, configuration = "commonJava"))
    add("commonResources", project(path = commonPath, configuration = "commonResources"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"])
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"])
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(configurations["commonResources"])
    from(configurations["commonResources"])
}

tasks.named<Javadoc>("javadoc") {
    dependsOn(configurations["commonJava"])
    source(configurations["commonJava"].filter {it.name.endsWith(".java")})
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(configurations["commonJava"])
    from(configurations["commonJava"])
    dependsOn(configurations["commonResources"])
    from(configurations["commonResources"])
}