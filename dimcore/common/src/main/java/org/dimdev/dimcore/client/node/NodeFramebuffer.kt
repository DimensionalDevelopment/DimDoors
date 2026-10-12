package org.dimdev.dimcore.client.node

interface NodeFramebuffer {
    val id: Int

    fun bind()

    fun status(): Int

    fun addColorAttachment(index: Int, texture: Int)

    fun addDepthAttachment(texture: Int)

    fun destroy()
}
