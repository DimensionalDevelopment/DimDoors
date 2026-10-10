package org.dimdev.dimdoors.client.render.node.iris

import net.irisshaders.iris.gl.sampler.SamplerHolder
import net.irisshaders.iris.gl.uniform.FloatSupplier
import net.irisshaders.iris.gl.uniform.UniformHolder
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency
import org.dimdev.dimdoors.client.render.node.NodeUniforms
import org.joml.Vector2f
import org.joml.Vector3f
import java.util.function.IntSupplier

class IrisUniforms(private val uniforms: UniformHolder, private val samplers: SamplerHolder) : NodeUniforms {
    override fun float(name: String, value: () -> Float) {
        uniforms.uniform1f(UniformUpdateFrequency.PER_FRAME, name, FloatSupplier { value() })
    }

    override fun int(name: String, value: () -> Int) {
        uniforms.uniform1i(UniformUpdateFrequency.PER_FRAME, name, IntSupplier { value() })
    }

    override fun vec2(name: String, value: () -> Vector2f) {
        uniforms.uniform2f(UniformUpdateFrequency.PER_FRAME, name, value)
    }

    override fun vec3(name: String, value: () -> Vector3f) {
        uniforms.uniform3f(UniformUpdateFrequency.PER_FRAME, name, value)
    }

    override fun sampler(name: String, texture: () -> Int) {
        samplers.addDynamicSampler(IntSupplier { texture() }, name)
    }
}
