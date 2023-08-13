architectury {
    common("forge", "fabric")
    platformSetupLoomIde()
}

loom.accessWidenerPath.set(file("src/main/resources/dimdoors.accesswidener"))

dependencies {
    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // Do NOT use other classes from fabric loader
    modImplementation("net.fabricmc:fabric-loader:${project.properties["fabric_loader_version"]}")
    // Remove the next line if you don't want to depend on the API
    modApi("dev.architectury:architectury:${project.properties["architectury_version"]}")
    modApi("me.shedaniel.cloth:cloth-config:9.0.94")
    modCompileOnlyApi("mcp.mobius.waila:wthit-api:fabric-${project.properties["wthitVersion"]}")

    modApi("com.flowpowered:flow-math:1.0.3")
    modApi("org.jgrapht:jgrapht-core:1.1.0")
    modApi("com.github.DimensionalDevelopment:poly2tri.java:0.1.1")

    modApi("com.sk89q.worldedit:worldedit-core:${project.properties["worldedit"]}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api:${project.properties["rei_version"]}")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin:${project.properties["rei_version"]}")
}

sourceSets.main {
    java {
        srcDir("src/main/schematics")
        srcDir("src/main/config")
    }
    resources.srcDirs("common/src/main/resources")
}

tasks.processResources {
    exclude("*")
}
