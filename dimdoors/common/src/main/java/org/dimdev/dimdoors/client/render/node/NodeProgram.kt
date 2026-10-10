package org.dimdev.dimdoors.client.render.node

interface NodeProgram {
    fun bind(context: NodeRenderContext)

    fun unbind()

    fun free()
}
