package org.dimdev.dimcore.client.models

import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.model.geom.builders.PartDefinition

fun create(u: Int, v: Int, block: PartDefinition.() -> Unit): LayerDefinition {
    return MeshDefinition().apply { root.apply { block.invoke(this) } }
        .run { LayerDefinition.create(this, u, v) }
}

data class PartBuilder(
    val name: String,
    val x: Float,
    val y: Float,
    val z: Float,
    val xRot: Float,
    val yRot: Float,
    val zRot: Float,
    val cubes: CubeListBuilder = CubeListBuilder.create(),
    val child: MutableList<PartBuilder> = mutableListOf()
) {
    fun box(originX: Float, originY: Float, originZ: Float, width: Float, height: Float, depth: Float) {
        cubes.addBox(originX, originY, originZ, width, height, depth)
    }

    fun uv(u: Int, v: Int) {
        cubes.texOffs(u, v)
    }

    fun part(name: String, x: Float, y: Float, z: Float, xRot: Float = 0f, yRot: Float = 0f, zRot: Float = 0f, block: PartBuilder.() -> Unit
    ) {
        PartBuilder(name, x, y, z, xRot, yRot, zRot).apply(block).also(child::add)
    }

    fun build(definition: PartDefinition): PartDefinition = definition.addOrReplaceChild(name, cubes, PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot)).also { part -> child.forEach { it.build(part) } }
}

fun PartDefinition.part(name: String, x: Float, y: Float, z: Float, xRot: Float = 0f, yRot: Float = 0f, zRot: Float = 0f, block: PartBuilder.() -> Unit) { PartBuilder(name, x, y, z, xRot, yRot, zRot).apply(block).build(this) }

