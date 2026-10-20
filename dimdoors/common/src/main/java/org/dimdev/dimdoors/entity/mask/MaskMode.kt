package org.dimdev.dimdoors.entity.mask

import com.mojang.serialization.Codec
import net.minecraft.util.StringRepresentable

enum class MaskMode(private val serializedName: String) : StringRepresentable {
    GUARD("guard"),
    PATROL("patrol"),
    WANDER("wander"),
    CHASE("chase"),
    STUNNED("stunned");

    val isPassive: Boolean
        get() = this == MaskMode.GUARD || this == MaskMode.PATROL || this == MaskMode.WANDER

    override fun getSerializedName() = serializedName
    
    companion object {
        val CODEC: Codec<MaskMode> = StringRepresentable.fromEnum { entries.toTypedArray() }
    }
}
