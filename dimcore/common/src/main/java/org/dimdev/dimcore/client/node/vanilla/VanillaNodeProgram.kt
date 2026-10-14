package org.dimdev.dimcore.client.node.vanilla

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ShaderInstance
import org.dimdev.dimcore.client.node.NodeProgram
import org.dimdev.dimcore.client.node.NodeUniforms

class VanillaNodeProgram(private val shader: ShaderInstance, declare: (NodeUniforms) -> Unit) : NodeProgram {
    private val uniforms = VanillaUniforms(shader).also(declare)

    override fun bind() {
        shader.setDefaultUniforms(VertexFormat.Mode.QUADS, RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), Minecraft.getInstance().window)
        shader.apply()
        uniforms.apply()
    }

    override fun unbind() = shader.clear()

    override fun free() = uniforms.close()
}
