@file:Suppress("UNUSED")

package com.kitakkun.backintime.convention

import com.kitakkun.backintime.convention.extension.BackInTimePublicationExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import gradle_conventions.convention.BuildConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign

class BackInTimePublicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create<BackInTimePublicationExtension>("backintimePublication")

            with(pluginManager) {
                apply("com.vanniktech.maven.publish")
            }

            afterEvaluate {
                val extension = extensions.getByType(BackInTimePublicationExtension::class.java)
                val artifactId = extension.artifactId
                if (artifactId.isBlank()) error("Artifact ID must be specified.")

                configure<MavenPublishBaseExtension> {
                    coordinates(
                        groupId = "com.kitakkun.backintime",
                        artifactId = artifactId,
                        version = BuildConfig.VERSION,
                    )

                    pom {
                        name.set("back-in-time")
                        description.set("Kondition ensure that your Kotlin code runs under some conditions are met. It inserts code to verify conditions for value parameters or variables at compile time.")
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
        }
    }
}