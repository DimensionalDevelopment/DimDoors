package org.dimdev.dimcore.client.node

import net.minecraft.resources.ResourceLocation

object NodeOverrides {
    private val programs = mutableMapOf<ResourceLocation, NodeProgram?>()
    private val framebuffers = mutableMapOf<ResourceLocation, NodeFramebuffer>()
    private val composites = mutableMapOf<ResourceLocation, NodeProgram?>()
    private val compositeFramebuffers = mutableMapOf<ResourceLocation, NodeFramebuffer>()

    fun bind(node: ResourceLocation, program: NodeProgram?, framebuffer: NodeFramebuffer?) {
        programs[node] = program
        framebuffers -= node
        framebuffer?.let { framebuffers[node] = it }
    }

    fun bindComposite(node: ResourceLocation, program: NodeProgram?, framebuffer: NodeFramebuffer?) {
        composites[node] = program
        compositeFramebuffers -= node
        framebuffer?.let { compositeFramebuffers[node] = it }
    }

    fun unbind(node: ResourceLocation) {
        programs -= node
        framebuffers -= node
        composites -= node
        compositeFramebuffers -= node
    }

    fun bound(node: ResourceLocation): Boolean = node in programs

    fun compositeBound(node: ResourceLocation): Boolean = node in composites

    fun program(node: ResourceLocation): NodeProgram? = programs[node]

    fun composite(node: ResourceLocation): NodeProgram? = composites[node]

    fun compositeFramebuffer(node: ResourceLocation): NodeFramebuffer? = compositeFramebuffers[node]

    fun framebuffer(node: ResourceLocation): NodeFramebuffer? = framebuffers[node]
}
