import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.5.30"
    id("io.papermc.paperweight.userdev") version "1.3.6"
}

val mcVer = "1.18.2"

base {
    archivesName.set("cmdlib")
    group = "net.lepinoid"
    version = "$mcVer+1"
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
    paperDevBundle("${mcVer}-R0.1-SNAPSHOT")
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
    repositories {
        maven {
            url = uri("${System.getProperty("user.home")}/lepinoid/maven-repo")
            println(uri("${System.getProperty("user.home")}/lepinoid/maven-repo"))
        }
    }
}