package org.dimdev.dimdoors.client

import net.minecraft.client.renderer.PostPass

interface PostChainExt {
    val passes: List<PostPass>
}
