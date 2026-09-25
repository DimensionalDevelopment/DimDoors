package org.dimdev.dimdoors.rift.registry

interface VertexProvider {
    fun collectVertices(): MutableList<out RegistryVertex>
}
