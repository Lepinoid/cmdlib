import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.9.0"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("org.jmailen.kotlinter") version "3.15.0"
}

val mcVer = "1.20.1"

base {
    archivesName.set("cmdlib")
    group = "net.lepinoid"
    version = "$mcVer+build.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    paperweight.paperDevBundle("${mcVer}-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.base.archivesName.get()
            version = project.version.toString()
            from(components["java"])
            artifact(sourcesJar)
            pom {
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://raw.githubusercontent.com/Lepinoid/cmdlib/develop/LICENSE")
                    }
                }
            }
        }
    }
    val publishTargetPath = System.getenv()["PUBLISH_PATH"]
    if (publishTargetPath != null) {
        repositories {
            maven {
                url = uri(publishTargetPath)
            }
        }
    }
}