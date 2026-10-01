package org.dimdev.dimdoors.block.entity

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.ContainerHelper
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimdoors.recipe.ModRecipeTypes
import org.dimdev.dimdoors.recipe.TesselatingRecipe
import org.dimdev.dimdoors.screen.TessellatingContainer
import org.dimdev.dimdoors.sound.ModSoundEvents
import kotlin.jvm.optionals.getOrNull

class TesselatingLoomBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModBlockEntityTypes.TESSELATING_LOOM.value(), pos, state), MenuProvider, WorldlyContainer {
    var weaveTime: Int = 0
    var weaveTimeTotal: Int = 0

    val dataAccess: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return when (index) {
                DATA_WEAVING_TIME -> this@TesselatingLoomBlockEntity.weaveTime
                DATA_WEAVING_TIME_TOTAL -> this@TesselatingLoomBlockEntity.weaveTimeTotal
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                DATA_WEAVING_TIME -> this@TesselatingLoomBlockEntity.weaveTime = value
                DATA_WEAVING_TIME_TOTAL -> this@TesselatingLoomBlockEntity.weaveTimeTotal = value
            }
        }

        override fun getCount(): Int {
            return NUM_DATA_VALUES
        }
    }

    var inventory: NonNullList<ItemStack> = NonNullList.withSize(9, ItemStack.EMPTY)
    var output: ItemStack = ItemStack.EMPTY
    private var cachedRecipe: RecipeHolder<TesselatingRecipe>? = null
    private val openContainers: MutableList<TessellatingContainer> = ArrayList<TessellatingContainer>()

    private val recipesUsed = Object2IntOpenHashMap<ResourceLocation?>()

    override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(nbt, provider)
        val inventoryTag = CompoundTag()
        ContainerHelper.saveAllItems(inventoryTag, inventory, provider)
        if (!output.isEmpty) inventoryTag.put("Output", output.save(provider, CompoundTag()))
        nbt.put(INVENTORY_TAG, inventoryTag)
        nbt.putInt(WEAVE_TIME_TAG, this.weaveTime)
        nbt.putInt(WEAVE_TIME_TOTAL_TAG, this.weaveTimeTotal)
    }

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(nbt, provider)

        val inventoryTag = nbt.getCompound(INVENTORY_TAG)
        ContainerHelper.loadAllItems(inventoryTag, this.inventory, provider)
        this.output = ItemStack.parseOptional(provider, inventoryTag.getCompound("Output"))
        this.weaveTime = nbt.getInt(WEAVE_TIME_TAG)
        this.weaveTimeTotal = nbt.getInt(WEAVE_TIME_TOTAL_TAG)
    }

    override fun getDisplayName(): MutableComponent = Component.translatable(blockState.block.descriptionId)

    override fun createMenu(syncId: Int, inv: Inventory, player: Player) = TessellatingContainer(syncId, this, inv, dataAccess)

    override fun getSlotsForFace(dir: Direction) = SLOTS

    override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack, dir: Direction?) = slot != 9

    override fun canTakeItemThroughFace(slot: Int, stack: ItemStack, dir: Direction) = slot == 9

    override fun canPlaceItem(slot: Int, stack: ItemStack) = slot != 9

    override fun isEmpty(): Boolean {
        for (stack in this.inventory) {
            if (!stack.isEmpty) return false
        }

        return output.isEmpty
    }

    override fun getContainerSize() = 10

    override fun getItem(slot: Int): ItemStack = when {
        slot < 9 -> this.inventory[slot]
        !output.isEmpty -> output
        else -> ItemStack.EMPTY
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        if (slot == 9) {
            return output.split(amount)
        }
        return ContainerHelper.removeItem(this.inventory, slot, amount)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        if (slot == 9) {
            val output = this.output
            this.output = ItemStack.EMPTY
            return output
        }
        return ContainerHelper.takeItem(this.inventory, slot)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (slot == 9) {
            output = stack
            return
        }

        inventory[slot] = stack
        setChanged()
    }

    override fun setChanged() {
        super.setChanged()
        for (c in openContainers) c.slotsChanged(this)
    }

    fun addOpenContainer(container: TessellatingContainer?) {
        if (!this.openContainers.contains(container)) {
            this.openContainers.add(container!!)
        }
    }

    fun removeOpenContainer(container: TessellatingContainer?) {
        this.openContainers.remove(container)
    }

    override fun stillValid(player: Player) = player.onPos.distSqr(this.worldPosition) <= 64.0

    override fun clearContent() {
        this.inventory.clear()
    }

    private val currentRecipe: RecipeHolder<TesselatingRecipe>? get() = if (this.level == null || this.isEmpty()) null else cachedRecipe?.id?.let(::getRecipe)?.takeIf { it.value().matches(this.asCraftInput(), level!!) } ?: this.recipe

    fun getRecipe(location: ResourceLocation) = this.level?.recipeManager?.getRecipeFor(ModRecipeTypes.TESSELATING, this.asCraftInput(), level!!, location)?.getOrNull()

    val recipe: RecipeHolder<TesselatingRecipe>?
        get() = this.level?.let {
            it.recipeManager.getRecipeFor(
                ModRecipeTypes.TESSELATING,
                this.asCraftInput(),
                it
            ).getOrNull()
        }

    private val weavingTotalTime: Int get() = this.currentRecipe?.value()?.weavingTime ?: DEFAULT_WEAVE_TIME

    fun serverTick() {
        val recipe = this.recipe

        if (cachedRecipe == null || cachedRecipe !== recipe) {
            cachedRecipe = recipe
            weaveTimeTotal = this.weavingTotalTime
        }

        if (cachedRecipe != null) {
            tryWeave()
        } else {
            tryDecrementCookTime()
        }
    }

    fun tryDecrementCookTime() {
        if (weaveTime > 0) {
            weaveTime = Mth.clamp(weaveTime - 2, 0, weaveTimeTotal)
            setChanged()
        }
    }


    private fun tryWeave() {

        val output = cachedRecipe?.value()?.assemble(this.asCraftInput(), level!!.registryAccess()) ?: return

        if (canAcceptOutput(output)) {
            weaveTime++

            if (weaveTime >= weaveTimeTotal) {
                weaveTime = 0
                cachedRecipe = null

                takeInputs()
                insertOutput(output)
            } else if (weaveTime % 60 == 0) {
                level!!.playSound(null, this.blockPos, ModSoundEvents.TESSELATING_WEAVE, SoundSource.BLOCKS)
            }

            setChanged()
        } else {
            tryDecrementCookTime()
        }
    }

    private fun asCraftInput() = CraftingInput.of(3, 3, inventory) //TODO: Investigate if cache can work.

    private fun insertOutput(output: ItemStack) {
        if (output.isEmpty) {
            return
        }

        if (output.isStackable) {
            for (slot in OUTPUT_SLOTS) {
                val existing = getItem(slot)

                if (!existing.isEmpty && ItemStack.isSameItemSameComponents(output, existing)) {
                    val total = existing.count + output.count

                    if (total <= existing.maxStackSize) {
                        output.count = 0
                        existing.count = total
                    } else if (existing.count < existing.maxStackSize) {
                        output.shrink(existing.maxStackSize - existing.count)
                        existing.count = existing.maxStackSize
                    }
                }

                if (output.isEmpty) {
                    return
                }
            }
        }

        for (slot in OUTPUT_SLOTS) {
            if (getItem(slot).isEmpty) {
                setItem(slot, output.split(output.count))
            }
        }
    }

    private fun takeInputs() {
        for (slot in INPUT_SLOTS) {
            val stack = getItem(slot)
            val item = stack.item

            stack.shrink(1)

            if (stack.isEmpty) {
                val newStack = item.craftingRemainingItem?.defaultInstance ?: ItemStack.EMPTY
                setItem(slot, newStack)
            }
        }

        setChanged()
    }

    private fun canAcceptOutput(output: ItemStack): Boolean {
        var remianingOutput = output.count

        for (slot in OUTPUT_SLOTS) {
            val existing = getItem(slot)

            if (existing.isEmpty) return true

            if (output.isStackable && ItemStack.isSameItemSameComponents(existing, output)) {
                if (existing.count + remianingOutput <= existing.maxStackSize) {
                    return true
                } else if (existing.count < existing.maxStackSize) {
                    remianingOutput -= existing.maxStackSize - existing.count
                }
            }

            if (remianingOutput == 0) {
                return true
            }
        }

        return false
    }

    override fun getUpdatePacket() = ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(provider: HolderLookup.Provider) = this.saveWithFullMetadata(provider)

    companion object {
        const val DATA_WEAVING_TIME: Int = 0
        const val DATA_WEAVING_TIME_TOTAL: Int = 1
        const val NUM_DATA_VALUES: Int = 2

        private val OUTPUT_SLOTS = intArrayOf(9)
        private val INPUT_SLOTS = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)

        private val SLOTS = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        private const val DEFAULT_WEAVE_TIME = 200
        private const val INVENTORY_TAG = "Inventory"
        private const val WEAVE_TIME_TAG = "WeaveTime"
        private const val WEAVE_TIME_TOTAL_TAG = "WeaveTimeTotal"
    }
}
