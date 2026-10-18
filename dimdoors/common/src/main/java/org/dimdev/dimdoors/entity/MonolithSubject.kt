package org.dimdev.dimdoors.entity

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import org.dimdev.dimcore.api.errata.schema.BoolNode
import org.dimdev.dimcore.api.errata.schema.FieldNode
import org.dimdev.dimcore.api.errata.schema.Presence
import org.dimdev.dimcore.api.errata.schema.StructNode
import org.dimdev.dimcore.api.errata.subject.EntitySubject
import org.dimdev.dimdoors.DimensionalDoors

class MonolithSubject(monolith: MonolithEntity) : EntitySubject<MonolithEntity>(monolith) {
    override val title: Component get() = entity.name

    override fun schema(player: ServerPlayer): StructNode {
        val fields = listOf(
            FieldNode("solid", BoolNode(entity.solid), Presence.Required, false, "Solid")
        )

        return StructNode(
            DimensionalDoors.id("monolith"),
            fields
        )
    }

    override fun read(player: ServerPlayer): CompoundTag {
        val tag = CompoundTag()
        tag.putBoolean("solid", entity.solid)
        return tag
    }

    override fun write(player: ServerPlayer, value: CompoundTag, paths: List<List<String>>): DataResult<Unit> {
        if(value.contains(SOLID)) {
            entity.solid = value.getBoolean(SOLID)
        }

        return DataResult.success(Unit)
    }

    private fun ops(player: ServerPlayer): DynamicOps<Tag> = player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE)

    companion object {
        private const val SOLID = "solid"
    }
}