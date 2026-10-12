package org.dimdev.dimcore.client.node.vanilla

import com.mojang.blaze3d.shaders.Uniform
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.ShaderInstance
import org.dimdev.dimcore.client.node.NodeUniforms
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL13

class VanillaUniforms(private val shader: ShaderInstance) : NodeUniforms, AutoCloseable {
    private val uniforms = mutableListOf<Uniform>()
    private val uploads = mutableListOf<() -> Unit>()
    private var units = 0

    private fun uniform(name: String, type: Int, count: Int): Uniform =
        Uniform(name, type, count, shader).also {
            it.location = Uniform.glGetUniformLocation(shader.id, name)
            uniforms += it
        }

    override fun float(name: String, value: () -> Float) {
        val uniform = uniform(name, Uniform.UT_FLOAT1, 1)
        uploads += { uniform.set(value()); uniform.upload() }
    }

    override fun int(name: String, value: () -> Int) {
        val uniform = uniform(name, Uniform.UT_INT1, 1)
        uploads += { uniform.set(value()); uniform.upload() }
    }

    override fun vec2(name: String, value: () -> Vector2f) {
        val uniform = uniform(name, Uniform.UT_FLOAT2, 2)
        uploads += { value().let { uniform.set(it.x, it.y) }; uniform.upload() }
    }

    override fun vec3(name: String, value: () -> Vector3f) {
        val uniform = uniform(name, Uniform.UT_FLOAT3, 3)
        uploads += { value().let { uniform.set(it.x, it.y, it.z) }
            uniform.upload() }
    }

    override fun sampler(name: String, texture: () -> Int) {
        val location = Uniform.glGetUniformLocation(shader.id, name)
        if (location == -1) return
        val unit = units++
        uploads += {
            RenderSystem.activeTexture(GL13.GL_TEXTURE0 + unit)
            RenderSystem.bindTexture(texture())
            Uniform.uploadInteger(location, unit)
        }
    }

    fun apply() {
        uploads.forEach { it() }
        RenderSystem.activeTexture(GL13.GL_TEXTURE0)
    }

    override fun close() = uniforms.forEach(Uniform::close)
}
