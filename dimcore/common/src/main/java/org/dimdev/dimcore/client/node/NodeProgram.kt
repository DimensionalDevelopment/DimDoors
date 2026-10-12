package org.dimdev.dimcore.client.node

interface NodeProgram {
    fun bind(context: NodeRenderContext)

    fun unbind()

    fun free()
}
