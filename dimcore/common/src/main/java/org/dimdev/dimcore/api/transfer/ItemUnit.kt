package org.dimdev.dimcore.api.transfer

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

data class ItemUnit(val item: Item, override val components: DataComponentPatch, override val amount: Long) : Unit<ItemUnit> {
    constructor(item: Item, amount: Long) : this(item, DataComponentPatch.EMPTY, amount)

    override val resource: Any get() = item

    fun toStack(): ItemStack {
        val stack = ItemStack(item, amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        stack.applyComponents(components)
        return stack
    }

    override fun withAmount(amount: Long): ItemUnit = copy(amount = amount)

    companion object {
        @JvmField
        val CODEC: Codec<ItemUnit> = RecordCodecBuilder.create { instance ->
            instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemUnit::item),
                DataComponentPatch.CODEC.fieldOf("components").forGetter(ItemUnit::components),
                Codec.LONG.fieldOf("amount").forGetter(ItemUnit::amount)
            ).apply(instance, ::ItemUnit)
        }

        private val EMPTY = ItemUnit(Items.AIR, 0)

        val ItemStack.unit: ItemUnit get() = TransferType.ITEM.create(item, componentsPatch, count.toLong())

        fun of(stack: ItemStack): ItemUnit = stack.unit

        fun empty(): ItemUnit = EMPTY
    }
}
