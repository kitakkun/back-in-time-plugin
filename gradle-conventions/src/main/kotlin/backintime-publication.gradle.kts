import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

open class BackInTimePublicationExtension {
    var artifactId: String = ""
}

extensions.create<BackInTimePublicationExtension>("backintimePublication")

with(pluginManager) {
    apply("com.vanniktech.maven.publish")
}

afterEvaluate {
    val extension = extensions.getByType(BackInTimePublicationExtension::class.java)
    val artifactId = extension.artifactId
    if (artifactId.isBlank()) error("Artifact ID must be specified.")

    configure<MavenPublishBaseExtension> {
        coordinates(artifactId = artifactId)

        pom {
            name.set("back-in-time")
            description.set("Kotlin Compiler Plugin to make your program back-in-time debuggable.")
            inceptionYear.set("2024")
            url.set("https://github.com/kitakkun/back-in-time-plugin")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://github.com/kitakkun/back-in-time-plugin/blob/master/LICENSE")
                    distribution.set("repo")
                }
                developers {
                    developer {
                        id.set("kitakkun")
                        name.set("kitakkun")
                        url.set("https://github.com/kitakkun")
                    }
                }
                scm {
                    url.set("https://github.com/kitakkun/back-in-time-plugin")
                    connection.set("scm:git:git://github.com/kitakkun/back-in-time-plugin.git")
                    developerConnection.set("scm:git:ssh://git@github.com/kitakkun/back-in-time-plugin.git")
                }
            }
        }

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()

        // avoid failure when executing publishToMavenLocal
        tasks.withType(Sign::class).configureEach {
            onlyIf {
                !gradle.startParameter.taskNames.contains("publishToMavenLocal")
            }
        }
    }
}
