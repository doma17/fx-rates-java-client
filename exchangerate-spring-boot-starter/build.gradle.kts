plugins {
    `java-library`
    `maven-publish`
    signing
}

description = "Optional Spring Boot starter for fx-rates-java-client"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":fx-rates-core"))

    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.5.11")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.5.11")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.11")

    testImplementation(platform("org.junit:junit-bom:5.14.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.springframework.boot:spring-boot-autoconfigure:3.5.11")
    testImplementation("org.springframework.boot:spring-boot-test:3.5.11")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "fx-rates-java-client-spring-boot-starter"
            from(components["java"])

            pom {
                name.set("fx-rates-java-client-spring-boot-starter")
                description.set(project.description)
                url.set("https://github.com/doma17/fx-rates-java-client")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("doma17")
                        name.set("doma17")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/doma17/fx-rates-java-client.git")
                    developerConnection.set("scm:git:ssh://git@github.com:doma17/fx-rates-java-client.git")
                    url.set("https://github.com/doma17/fx-rates-java-client")
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            val releasesRepoUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
                password = findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    val signingKey: String? = findProperty("signingKey") as String? ?: System.getenv("SIGNING_KEY")
    val signingPassword: String? = findProperty("signingPassword") as String? ?: System.getenv("SIGNING_PASSWORD")

    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}
