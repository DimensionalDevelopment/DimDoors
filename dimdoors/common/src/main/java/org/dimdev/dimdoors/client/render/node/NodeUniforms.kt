package org.dimdev.dimdoors.client.render.node

import org.joml.Vector2f
import org.joml.Vector3f

interface NodeUniforms {
    fun float(name: String, value: () -> Float)

    fun int(name: String, value: () -> Int)

    fun vec2(name: String, value: () -> Vector2f)

    fun vec3(name: String, value: () -> Vector3f)

    fun sampler(name: String, texture: () -> Int)
}
