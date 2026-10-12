package org.dimdev.dimcore.client.node

import net.minecraft.resources.ResourceLocation

object NodeOverrides {
    private val programs = mutableMapOf<Pair<ResourceLocation, NodeProgramType>, NodeProgram>()
    private val framebuffers = mutableMapOf<Pair<ResourceLocation, NodeProgramType>, NodeFramebuffer>()

    fun bind(node: ResourceLocation, type: NodeProgramType, program: NodeProgram, framebuffer: NodeFramebuffer?) {
        programs[node to type] = program
        framebuffer?.let { framebuffers[node to type] = it }
    }

    fun unbind(node: ResourceLocation, kind: NodeProgramType) {
        programs -= node to kind
        framebuffers -= node to kind
    }

    fun program(node: ResourceLocation, type: NodeProgramType): NodeProgram? = programs[node to type]

    fun framebuffer(node: ResourceLocation, type: NodeProgramType): NodeFramebuffer? = framebuffers[node to type]
}
