import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.5.30"
    id("fr.il_totore.manadrop") version "0.4-SNAPSHOT"
}

val mcVer = "1.17.1"

base {
    archivesName.set("cmdlib")
    group = "net.lepinoid"
    version = "$mcVer+1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

repositories {
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    val releaseVer = "R0.1-SNAPSHOT"
    implementation("io.papermc.paper:paper-api:$mcVer-$releaseVer")
    implementation("io.papermc.paper:paper-mojangapi:$mcVer-$releaseVer")
    implementation("org.spigotmc:spigot:$mcVer-$releaseVer")
}

tasks.withType<fr.il_totore.manadrop.spigot.task.BuildTools> {
    versions(mcVer)
    workDir = File("run")
    mavenPath = "/usr/bin/mvn"
}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "16"
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