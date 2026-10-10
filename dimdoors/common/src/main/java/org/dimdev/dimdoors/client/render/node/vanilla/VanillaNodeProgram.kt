package org.dimdev.dimdoors.client.render.node.vanilla

import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ShaderInstance
import org.dimdev.dimdoors.client.render.node.NodeProgram
import org.dimdev.dimdoors.client.render.node.NodeRenderContext
import org.dimdev.dimdoors.client.render.node.NodeUniforms
import org.joml.Matrix4f

class VanillaNodeProgram(private val shader: ShaderInstance, declare: (NodeUniforms) -> Unit) : NodeProgram {
    private val uniforms = VanillaUniforms(shader).also(declare)

    override fun bind(context: NodeRenderContext) {
        shader.setDefaultUniforms(VertexFormat.Mode.QUADS, Matrix4f(context.modelView), Matrix4f(context.projection), Minecraft.getInstance().window)
        shader.apply()
        uniforms.apply()
    }

    override fun unbind() = shader.clear()

    override fun free() = uniforms.close()
}
