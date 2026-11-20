package org.dimdev.dimdoors.rift.item

import net.minecraft.core.component.DataComponents
import net.minecraft.world.Container
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.BundleContents
import net.minecraft.world.item.component.ItemContainerContents
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.event.ItemHolderEvents
import org.dimdev.dimcore.api.ext.nested
import org.dimdev.dimcore.api.ext.stacks
import org.dimdev.dimdoors.item.ModDataComponentTypes
import org.dimdev.dimdoors.rift.RiftRegistry
import org.dimdev.dimdoors.subsystem.graph.RegistryVertex
import org.dimdev.dimdoors.subsystem.graph.RegistryVertices
import org.dimdev.dimdoors.subsystem.graph.RiftGraph
import org.dimdev.dimdoors.subsystem.registry.SubsystemTypes
import java.util.*


class ItemRiftRegistry(
    rifts: Map<UUID, Holder> = emptyMap(),
    placeholders: Collection<UUID> = emptySet()) : RiftRegistry<Holder, ItemStack, ItemRiftRegistry>(rifts, placeholders) {

    override fun type() = SubsystemTypes.ITEM_RIFT
    override val events: RiftEvents<Holder> get() = EVENTS
    override val logger: Logger get() = LOGGER

    fun track(id: UUID, holder: Holder?) {
        val inverse = bimap.inverse()

        val previous = if (holder == null) inverse.remove(id) else inverse.put(id, holder)
        if (previous == holder) return

        setDirty()
        when {
            previous == null -> EVENTS.riftAddedEvent.invoker().riftAdded(id, holder!!)
            holder == null -> EVENTS.riftRemoveEvent.invoker().riftRemoved(id, previous)
            else -> EVENTS.riftMoveEvent.invoker().riftMoved(id, previous, holder)
        }
    }

    override fun attach(stored: ItemStack, ref: Holder): UUID = getRiftOrPlaceholder(ref).also { stored.set(ModDataComponentTypes.TRACKED_ITEM, it) }

    override fun detach(stored: ItemStack) {
        stored.remove(ModDataComponentTypes.RIFT_DATA)
        val id = stored.remove(ModDataComponentTypes.TRACKED_ITEM) ?: return
        track(id, null)
    }

    override fun storedAt(ref: Holder): ItemStack? = idAt(ref)?.let { ref.findStack(it) }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger()

        val CODEC = codec(Holder.CODEC, "holders", ::ItemRiftRegistry)

        val EVENTS = object : RiftEvents<Holder>() {
            override val vertexType: RegistryVertex get() = RegistryVertices.ITEM_RIFT

            override fun registerEvents(riftGraph: RiftGraph) {
                super.registerEvents(riftGraph)
                platform.onEntityJoinLevel { _, entity -> if (entity is ItemEntity) track(entity.item, Holder.entity(entity)) }
                platform.onItemPickup { player, stack -> if (!player.level().isClientSide) track(stack, Holder.entity(player)) }
                ItemHolderEvents.ENTITY.register { entity, stack -> if (!entity.level().isClientSide) track(stack, Holder.entity(entity)) }
                ItemHolderEvents.CONTAINER.register { container, stack -> if (stack.has(ModDataComponentTypes.TRACKED_ITEM) || stack.has(DataComponents.CONTAINER) || stack.has(DataComponents.BUNDLE_CONTENTS)) Holder.of(container)?.let { track(stack, it) } }
                platform.onPlayerJoin { player -> Holder.offline.remove(player.uuid) }
                platform.onAfterBlockPlace { level, pos, _, _ ->
                    val container = level.getBlockEntity(pos) as? Container ?: return@onAfterBlockPlace
                    val holder = Holder.of(container) ?: return@onAfterBlockPlace
                    container.stacks.forEach { track(it, holder) }
                }
            }

            private fun track(stack: ItemStack, holder: Holder) = stack.nested().mapNotNull { it.get(ModDataComponentTypes.TRACKED_ITEM) }
                .filter { instance.locationOf(it) != null }
                .forEach { instance.track(it, holder) }



            fun update(stack: ItemStack, id: UUID, edit: (ItemStack) -> Unit): Boolean {
                if (stack.get(ModDataComponentTypes.TRACKED_ITEM) == id) return true.also { edit(stack) }

                stack.get(DataComponents.CONTAINER)?.stream()?.toList()?.takeIf { items -> items.any { update(it, id, edit) } }?.let {
                    stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(it))
                    return true
                }

                stack.get(DataComponents.BUNDLE_CONTENTS)?.itemCopyStream()?.toList()?.takeIf { items -> items.any { update(it, id, edit) } }?.let {
                    stack.set(DataComponents.BUNDLE_CONTENTS, BundleContents(it))
                    return true
                }

                return false
            }
        }

        fun find(stack: ItemStack, id: UUID): ItemStack? = stack.nested().firstOrNull { it.get(ModDataComponentTypes.TRACKED_ITEM) == id }

        fun rift(stack: ItemStack): Holder? = stack.get(ModDataComponentTypes.TRACKED_ITEM)?.let { instance.locationOf(it) }

        val instance: ItemRiftRegistry get() = getInstance(SubsystemTypes.ITEM_RIFT)!!
    }
}
