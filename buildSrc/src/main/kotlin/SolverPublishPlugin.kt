import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

class SolverPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply("maven-publish")
        project.extensions.configure<PublishingExtension>("publishing") {
            publications {
                register("maven", MavenPublication::class.java) {
                    groupId = project.property("publishing.groupId") as String
                    artifactId = project.name
                    version = project.property("publishing.version") as String
                    from(project.components.getByName("java"))
                }
            }
        }
    }
}
