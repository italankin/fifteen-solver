repositories {
    gradlePluginPortal()
}

plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("solver-publish") {
            id = "solver-publish"
            implementationClass = "SolverPublishPlugin"
        }
    }
}
