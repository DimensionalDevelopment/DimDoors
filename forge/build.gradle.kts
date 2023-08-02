plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    forge()
}

val minecraftVersion = project.properties["minecraft_version"] as String

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("dimdoors-common.mixins.json")
        mixinConfig("dimdoors.mixins.json")
    }
}

configurations {
    create("common")
    create("shadowCommon")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentForge").extendsFrom(configurations["common"])
}

dependencies {
    forge("net.minecraftforge:forge:$minecraftVersion-${project.properties["forge_version"]}")
    modApi("dev.architectury:architectury-forge:${project.properties["architectury_version"]}")

    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowCommon"(project(":common", "transformProductionForge")) { isTransitive = false }
    modApi("me.shedaniel.cloth:cloth-config-forge:9.0.94")

    include("com.flowpowered:flow-math:1.0.3")
    include("org.jgrapht:jgrapht-core:1.1.0")
    include("com.github.DimensionalDevelopment:poly2tri.java:0.1.1")

    modCompileOnly("mcp.mobius.waila:wthit-api:forge-${project.properties["wthitVersion"]}")
    modApi("mcp.mobius.waila:wthit:forge-${project.properties["wthitVersion"]}")
    modApi("lol.bai:badpackets:forge-0.4.1")
    modApi("com.sk89q.worldedit:worldedit-forge-mc${minecraftVersion}:${project.properties["worldedit"]}")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }

        from(rootProject.file("common/src/main/resources")){
            include("**/**")
            duplicatesStrategy = DuplicatesStrategy.WARN
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")
        configurations = listOf(project.configurations.getByName("shadowCommon"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }

    jar.get().archiveClassifier.set("dev")

    sourcesJar {
        val commonSources = project(":common").tasks.sourcesJar
        dependsOn(commonSources)
        from(commonSources.get().archiveFile.map { zipTree(it) })
    }
}

components {
    java.run {
        if (this is AdhocComponentWithVariants)
            withVariantsFromConfiguration(project.configurations.shadowRuntimeElements.get()) { skip() }
    }
}

sourceSets.main.get().resources.srcDirs(project(":common").file("src/main/generated"))
