package org.dimdev.dimcore.api.event

import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import org.dimdev.dimcore.util.util.SimpleEvent

object ItemHolderEvents {
    @JvmField
    val CONTAINER: SimpleEvent<(Container, Int, ItemStack) -> Unit> = SimpleEvent.of { listeners -> { container, index, stack ->
            listeners.forEach { listener -> listener.invoke(container, index, stack) }
        }
    }

    @JvmField
    val CONTAINER_DESTROYED: SimpleEvent<(Container, Int) -> Unit> = SimpleEvent.of { listeners -> { container, index ->
            listeners.forEach { listener -> listener.invoke(container, index) }
        }
    }

    @JvmField
    val ENTITY: SimpleEvent<(Entity, Int, ItemStack) -> Unit> = SimpleEvent.of { listeners -> { entity, index, stack ->
            listeners.forEach { listener -> listener.invoke(entity, index, stack) }
        }
    }

    @JvmField
    val ENTITY_INVENTORY: SimpleEvent<(Entity, Int, ItemStack) -> Unit> = SimpleEvent.of { listeners -> { entity, index, stack ->
            listeners.forEach { listener -> listener.invoke(entity, index, stack) }
        }
    }
}
