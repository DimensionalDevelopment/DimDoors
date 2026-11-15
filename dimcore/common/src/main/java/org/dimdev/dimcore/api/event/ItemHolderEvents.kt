package org.dimdev.dimcore.api.event

import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import org.dimdev.dimcore.api.util.SimpleEvent

object ItemHolderEvents {
    @JvmField
    val CONTAINER: SimpleEvent<(Container, ItemStack) -> Unit> = SimpleEvent.of { listeners -> { container, stack ->
            listeners.forEach { listener -> listener.invoke(container, stack) }
        }
    }

    @JvmField
    val ENTITY: SimpleEvent<(Entity, ItemStack) -> Unit> = SimpleEvent.of { listeners -> { entity, stack ->
            listeners.forEach { listener -> listener.invoke(entity, stack) }
        }
    }
}
