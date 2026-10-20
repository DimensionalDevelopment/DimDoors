package org.dimdev.dimdoors.entity.mask

import net.minecraft.util.StringRepresentable

enum class MaskMode(private val serializedName: String) : StringRepresentable {
    GUARD("guard"),
    PATROL("patrol"),
    WANDER("wander");

    override fun getSerializedName() = serializedName

    companion object {
        @JvmField val CODEC: StringRepresentable.EnumCodec<MaskMode> = StringRepresentable.fromEnum { entries.toTypedArray() }
    }
}
