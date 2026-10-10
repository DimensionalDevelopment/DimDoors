package org.dimdev.dimdoors.client.render.node

import net.minecraft.resources.ResourceLocation

object NodeOverrides {
    private val programs = mutableMapOf<Pair<ResourceLocation, NodeProgramKind>, NodeProgram>()
    private val framebuffers = mutableMapOf<Pair<ResourceLocation, NodeProgramKind>, NodeFramebuffer>()

    fun bind(node: ResourceLocation, kind: NodeProgramKind, program: NodeProgram, framebuffer: NodeFramebuffer?) {
        programs[node to kind] = program
        framebuffer?.let { framebuffers[node to kind] = it }
    }

    fun unbind(node: ResourceLocation, kind: NodeProgramKind) {
        programs -= node to kind
        framebuffers -= node to kind
    }

    fun program(node: ResourceLocation, kind: NodeProgramKind): NodeProgram? = programs[node to kind]

    fun framebuffer(node: ResourceLocation, kind: NodeProgramKind): NodeFramebuffer? = framebuffers[node to kind]
}
