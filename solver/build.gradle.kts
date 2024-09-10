plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
}

kotlin {
    jvmToolchain(17)
}

publishing {
    repositories {
        maven {
            url = uri(System.getenv("MVN_REPOSITORY") ?: "")
            credentials {
                username = System.getenv("MVN_USERNAME")
                password = System.getenv("MVN_PASSWORD")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            groupId = property("publishing.groupId") as String
            artifactId = project.name
            version = property("publishing.version") as String
            from(components["java"])
        }
    }
}

dependencies {
    api(project(":game"))

    api(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization)
    implementation(libs.eclipse.collections)

    testImplementation(libs.junit)
}
