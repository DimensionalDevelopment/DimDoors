package org.dimdev.dimcore.api

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.levelgen.carver.WorldCarver
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.util.DataValue

class DataValueType<T>(val defaultValue: () -> T, val codec: Codec<T>, val streamCodec: StreamCodec<in RegistryFriendlyByteBuf, T>?)

class CreativeTabType(val block: CreativeModeTab.Builder.() -> Unit)

class CreativeTab {
    lateinit var holder: Holder<CreativeModeTab>
    private val entries = mutableListOf<Pair<(() -> ItemLike)?, () -> Collection<ItemLike>>>()

    fun add(item: () -> ItemLike) {
        entries += null to { listOf(item()) }
    }

    fun addAllAfter(anchor: () -> ItemLike, items: () -> Collection<ItemLike>) {
        entries += anchor to items
    }

    fun fill(output: CreativeModeTab.Output) {
        val stacks = mutableListOf<ItemStack>()
        entries.filter { it.first == null }.forEach { (_, items) -> items().mapTo(stacks, ::ItemStack) }

        entries.filter { it.first != null }.forEach { (anchor, items) ->
            val anchorItem = anchor!!().asItem()
            val added = items().map(::ItemStack)
            val index = stacks.indexOfFirst { it.item == anchorItem }
            if (index >= 0) stacks.addAll(index + 1, added) else stacks.addAll(added)
        }

        stacks.forEach(output::accept)
    }
}

abstract class PlatformRegistry<T: Any>(registryKey: ResourceKey<Registry<T>>, registry: Registry<T>?, sided: ISided<*>) {
    constructor(registryKey: ResourceKey<Registry<T>>, sided: ISided<*>) : this(registryKey, null, sided)

    private val register: EntryRegister<T> = sided.entryRegister(registryKey, registry)
    val registry: Registry<T> = register.createRegistry()
    val modid = sided.modId()

    protected val queue = mutableListOf<Holder<T>>()

    /**
     * Creates a new entry in this registry.
     *
     * @param E The type of the entry.
     * @param name The name of the entry, this will be an [ResourceLocation.path].
     * @param entry The entry being added.
     * @return The entry created.
     */
    open fun <V : T> create(name: String, entry: () -> V): Holder<V> =
        register.holder(name, entry).also { queue.add(it.cast()) }

    /**
     * Handles the registration of this registry into the platform specific one.
     *
     * @param consumer The consumer that will handle the logic for registering every entry in this registry into the platform specific one.
     */
    open fun register() {}

    open fun allHolders(): Collection<Holder<T>> = this.queue.toList()


    /**
     * Returns a collection of every entry in this registry.
     *
     * @return The entries of this registry.
     */
    open fun all(): Collection<T> = allHolders().map { it.value() }.toList()

    abstract class EntryRegister<T> {
        abstract fun createRegistry(): Registry<T>
        abstract fun <V : T> holder(name: String, supplier: () -> V): Holder<V>
    }

    open class BlockPlatformRegistry(sided: ISided<*>) : PlatformRegistry<Block>(Registries.BLOCK, BuiltInRegistries.BLOCK, sided)
    open class ConfiguredCarverPlatformRegistry(sided: ISided<*>) : PlatformRegistry<WorldCarver<*>>(Registries.CARVER, BuiltInRegistries.CARVER, sided)
    open class ItemPlatformRegistry(sided: ISided<*>) : PlatformRegistry<Item>(Registries.ITEM, BuiltInRegistries.ITEM, sided)
    open class CreativeTabPlatformRegistry(sided: ISided<*>) : PlatformRegistry<Any>(KEY, sided) {
        fun create(name: String, block: CreativeModeTab.Builder.() -> Unit): CreativeTab {
            val tab = CreativeTab()
            tab.holder = (create(name) { CreativeTabType { block(); displayItems { _, output -> tab.fill(output) } } } as Holder<*>).cast()
            return tab
        }

        companion object {
            val KEY: ResourceKey<Registry<Any>> = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("dimcore", "creative_tab"))
        }
    }
    open class EntityTypePlatformRegistry(sided: ISided<*>) : PlatformRegistry<EntityType<*>>(Registries.ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE, sided) {
        fun <E : Entity> create(
            id: String,
            factory: EntityType.EntityFactory<E>,
            category: MobCategory,
            block: EntityType.Builder<E>.() -> Unit,
        ): Holder<EntityType<E>> = create(id) {
            EntityType.Builder.of(factory, category).also(block).build(id)
        }

    }
    open class BlockEntityTypePlatformRegistry(sided: ISided<*>) : PlatformRegistry<BlockEntityType<*>>(Registries.BLOCK_ENTITY_TYPE, BuiltInRegistries.BLOCK_ENTITY_TYPE, sided)
    open class DataComponentTypePlatformRegistry(sided: ISided<*>) : PlatformRegistry<DataComponentType<*>>(Registries.DATA_COMPONENT_TYPE, BuiltInRegistries.DATA_COMPONENT_TYPE, sided) {
        fun <T: Any, V : Codec<T>, U : StreamCodec<RegistryFriendlyByteBuf, T>> create(
            name: String,
            codec: V,
            streamCodec: U? = null
        ): Holder<DataComponentType<T>> {
            val builder = DataComponentType.builder<T>().persistent(codec)
            if (streamCodec != null) builder.networkSynchronized(streamCodec)
            builder.cacheEncoding()
            return create(name) { builder.build() }
        }
    }

    open class FluidPlatformRegistry(sided: ISided<*>) : PlatformRegistry<Fluid>(Registries.FLUID, BuiltInRegistries.FLUID, sided)
    open class DataValuePlatformRegistry(sided: ISided<*>) : PlatformRegistry<Any>(KEY, sided) {
        fun <T> create(name: String, defaultValue: () -> T, codec: Codec<T>, streamCodec: StreamCodec<in RegistryFriendlyByteBuf, T>? = null): DataValue<T> =
            (create(name) { DataValueType(defaultValue, codec, streamCodec) } as Holder<*>).value() as DataValue<T>

        companion object {
            val KEY: ResourceKey<Registry<Any>> = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("dimcore", "data_value"))
        }
    }
    open class MapCodecPlatformRegistry<B : MapCodecHasHolder<B>>(registryKey: ResourceKey<Registry<MapCodec<out B>>>, sided: ISided<*>) : PlatformRegistry<MapCodec<out B>>(registryKey, sided) {
        val codec: Codec<B> = this.registry.byNameCodec().dispatch({ it.type.value() }, { it })
    }
    open class TypePlatformRegistry<B : TypeHasHolder<B>>(registryKey: ResourceKey<Registry<Type<B>>>, sided: ISided<*>) : PlatformRegistry<Type<B>>(registryKey, sided) {
        fun <T : B> create(id: String, codec: MapCodec<T>, streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>? = null): Holder<Type<T>> = create(id) { Type(codec, streamCodec) }
        fun <T : B> create(id: String, codec: MapCodec<B>): Holder<Type<B>> = create(id) { Type(codec, null) }

        val codec = this.registry.byNameCodec().dispatch({ it.type.value() }, { it.codec })
        val streamCodec = ByteBufCodecs.registry(registryKey).dispatch({ it.type.value() }, { it.streamCodec })
    }

    open class BuilderTypePlatformRegistry<B : BuilderTypeHasHolder<B, C>, C : BuilderTypeHasHolder<B, C>>(registryKey: ResourceKey<Registry<BuilderType<B, C>>>, sided: ISided<*>) : PlatformRegistry<BuilderType<B, C>>(registryKey, sided) {
        fun <T : B, V: C> create(id: String, codec: MapCodec<T>, builderCodec: MapCodec<V>, streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>? = null): Holder<BuilderType<T, V>> = create(id) { BuilderType(codec, builderCodec, streamCodec) }

        val codec = this.registry.byNameCodec().dispatch({ it.type.value() }, { it.codec })
        val builderCodec = this.registry.byNameCodec().dispatch({ it.type.value() }, { it.builderCodec })
        val streamCodec = ByteBufCodecs.registry(registryKey)
    }

    open class BlockItemPlatformRegistry(protected val sided: ISided<*>) {
        protected val blocks = BlockPlatformRegistry(sided)
        protected val items = ItemPlatformRegistry(sided)

        class Builder(val name: String) {
            var blockFunction: (BlockBehaviour.Properties) -> Block = ::Block
            var blockProperties: () -> BlockBehaviour.Properties = { BlockBehaviour.Properties.of() }
            var itemFunction: ((Block, Item.Properties) -> BlockItem)? = null
            var itemProperties: Item.Properties.() -> Unit = {}
            var tab: CreativeTab? = null

            fun block(function: (BlockBehaviour.Properties) -> Block) {
                blockFunction = function
            }

            fun blockProperties(block: BlockBehaviour.Properties.() -> Unit) {
                blockProperties = { BlockBehaviour.Properties.of().apply(block) }
            }

            fun blockProperties(from: Block, block: BlockBehaviour.Properties.() -> Unit = {}) {
                blockProperties = { BlockBehaviour.Properties.ofFullCopy(from).apply(block) }
            }

            fun blockProperties(from: Holder<out Block>, block: BlockBehaviour.Properties.() -> Unit = {}) {
                blockProperties = { BlockBehaviour.Properties.ofFullCopy(from.value()).apply(block) }
            }

            fun item(function: (Block, Item.Properties) -> BlockItem = ::BlockItem, properties: Item.Properties.() -> Unit = {}) {
                itemFunction = function
                itemProperties = properties
            }

            fun tab(tab: CreativeTab) {
                this.tab = tab
                if (itemFunction == null) itemFunction = ::BlockItem
            }
        }

        fun create(name: String, block: Builder.() -> Unit = {}): Holder<Block> {
            val builder = Builder(name).apply(block)
            val blockHolder = blocks.create(name) { builder.blockFunction(builder.blockProperties()) }

            builder.itemFunction?.let { function ->
                val itemHolder = items.create(name) { function(blockHolder.value(), Item.Properties().apply(builder.itemProperties)) }
                builder.tab?.add { itemHolder.value() }
            }

            return blockHolder
        }

        fun createWithItem(name: String, block: Builder.() -> Unit = {}): Holder<Block> = create(name) {
            itemFunction = ::BlockItem
            block(this)
        }
    }
}
