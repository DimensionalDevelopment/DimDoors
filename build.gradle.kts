import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.3-SNAPSHOT" apply false
    idea
    java
}

val minecraftVersion = project.properties["minecraft_version"] as String

architectury.minecraft = minecraftVersion

subprojects {
    apply(plugin = "dev.architectury.loom")

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    loom.silentMojangMappingsLicense()

    repositories {
        mavenCentral()
        maven("https://maven.shedaniel.me/")
        maven("https://jitpack.io")
        maven("https://maven.bai.lol").content {
            includeGroup("lol.bai")
            includeGroup("mcp.mobius.waila")
        }
        maven("https://cursemaven.com").content { includeGroup("curse.maven") }
        maven("https://maven.enginehub.org/repo/")
        maven("https://maven.parchmentmc.org")
    }

    @Suppress("UnstableApiUsage")
    dependencies {
        "minecraft"("com.mojang:minecraft:$minecraftVersion")
        "mappings"(loom.layered{
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-$minecraftVersion:${project.properties["parchment"]}@zip")
        })
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")
    apply(plugin = "idea")


    base.archivesName.set(project.properties["archives_base_name"] as String)
    version = project.properties["mod_version"] as String
    group = project.properties["maven_group"] as String


    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java.withSourcesJar()
}
