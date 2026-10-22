package org.dimdev.dimdoors.block.entity

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectIterator
import net.minecraft.SharedConstants
import net.minecraft.Util
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.RecipeCraftingHolder
import net.minecraft.world.inventory.StackedContentsCompatible
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AbstractFurnaceBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.recipe.TesselatingRecipe
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern
import java.util.function.Consumer
import java.util.function.Function
import kotlin.concurrent.Volatile

abstract class AbstractFurnaceBlockEntity protected constructor(
    type: BlockEntityType<*>,
    pos: BlockPos,
    blockState: BlockState,
) : BaseContainerBlockEntity(type, pos, blockState), WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
    protected var items: NonNullList<ItemStack>
    var litTime: Int = 0
    var litDuration: Int = 0
    var cookingProgress: Int = 0
    var cookingTotalTime: Int = 0
    protected val dataAccess: ContainerData
    private val recipesUsed: Object2IntOpenHashMap<ResourceLocation>
    private val quickCheck: RecipeManager.CachedCheck<SingleRecipeInput, out AbstractCookingRecipe>

    init {
        this.items = NonNullList.withSize(3, ItemStack.EMPTY)
        this.dataAccess = object : ContainerData {
            override fun get(p_58431_: Int): Int = when (p_58431_) {
                0 -> this@AbstractFurnaceBlockEntity.litTime
                1 -> this@AbstractFurnaceBlockEntity.litDuration
                2 -> this@AbstractFurnaceBlockEntity.cookingProgress
                3 -> this@AbstractFurnaceBlockEntity.cookingTotalTime
                else -> 0
            }

            override fun set(p_58433_: Int, p_58434_: Int) {
                when (p_58433_) {
                    0 -> this@AbstractFurnaceBlockEntity.litTime = p_58434_
                    1 -> this@AbstractFurnaceBlockEntity.litDuration = p_58434_
                    2 -> this@AbstractFurnaceBlockEntity.cookingProgress = p_58434_
                    3 -> this@AbstractFurnaceBlockEntity.cookingTotalTime = p_58434_
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }
        this.recipesUsed = Object2IntOpenHashMap<Any>()
        this.quickCheck = RecipeManager.createCheck(recipeType)
    }

    private val isLit: Boolean
        get() = this.litTime > 0

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, this.items, registries)
        this.litTime = tag.getShort("BurnTime").toInt()
        this.cookingProgress = tag.getShort("CookTime").toInt()
        this.cookingTotalTime = tag.getShort("CookTimeTotal").toInt()
        this.litDuration = this.getBurnDuration(this.items[1])
        val compoundtag = tag.getCompound("RecipesUsed")

        for (s in compoundtag.getAllKeys()) {
            this.recipesUsed.put(ResourceLocation.parse(s), compoundtag.getInt(s))
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putShort("BurnTime", this.litTime.toShort())
        tag.putShort("CookTime", this.cookingProgress.toShort())
        tag.putShort("CookTimeTotal", this.cookingTotalTime.toShort())
        ContainerHelper.saveAllItems(tag, this.items, registries)
        val compoundtag = CompoundTag()
        this.recipesUsed.forEach { (p_187449_: ResourceLocation?, p_187450_: Int?) ->
            compoundtag.putInt(
                p_187449_.toString(),
                p_187450_!!
            )
        }
        tag.put("RecipesUsed", compoundtag)
    }

    protected open fun getBurnDuration(fuel: ItemStack): Int {
        if (fuel.isEmpty()) {
            return 0
        } else {
            val item = fuel.getItem()
            return (net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.getFuel()
                .getOrDefault(item, 0) as kotlin.Int?)!!
        }
    }

    override fun getSlotsForFace(side: Direction): IntArray {
        if (side == Direction.DOWN) {
            return net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.SLOTS_FOR_DOWN
        } else {
            return if (side == Direction.UP) net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.SLOTS_FOR_UP else net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.SLOTS_FOR_SIDES
        }
    }

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean {
        return this.canPlaceItem(index, itemStack)
    }

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean {
        return if (direction == Direction.DOWN && index == 1) stack.`is`(Items.WATER_BUCKET) || stack.`is`(Items.BUCKET) else true
    }

    override fun getContainerSize(): Int {
        return this.items.size
    }

    override fun getItems(): NonNullList<ItemStack> {
        return this.items
    }

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    override fun setItem(index: Int, stack: ItemStack) {
        val itemstack = this.items.get(index)
        val flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack)
        this.items.set(index, stack)
        stack.limitSize(this.getMaxStackSize(stack))
        if (index == 0 && !flag) {
            this.cookingTotalTime =
                net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.getTotalCookTime(
                    this.level,
                    this
                )
            this.cookingProgress = 0
            this.setChanged()
        }
    }

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean {
        if (index == 2) {
            return false
        } else if (index != 1) {
            return true
        } else {
            val itemstack = this.items.get(1)
            return net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.isFuel(stack) || stack.`is`(
                Items.BUCKET
            ) && !itemstack.`is`(Items.BUCKET)
        }
    }

    override fun setRecipeUsed(recipe: RecipeHolder<*>?) {
        if (recipe != null) {
            val resourcelocation = recipe.id()
            this.recipesUsed.addTo(resourcelocation, 1)
        }
    }

    override fun getRecipeUsed(): RecipeHolder<*>? {
        return null
    }

    override fun awardUsedRecipes(player: Player, items: MutableList<ItemStack?>) {
    }

    fun awardUsedRecipesAndPopExperience(player: ServerPlayer) {
        val list = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position())
        player.awardRecipes(list)

        for (recipeholder in list) {
            if (recipeholder != null) {
                player.triggerRecipeCrafted(recipeholder, this.items)
            }
        }

        this.recipesUsed.clear()
    }

    fun getRecipesToAwardAndPopExperience(level: ServerLevel, popVec: Vec3): MutableList<RecipeHolder<*>?> {
        val list: MutableList<RecipeHolder<*>?> = Lists.newArrayList<RecipeHolder<*>?>()
        val var4: ObjectIterator<*> = this.recipesUsed.object2IntEntrySet().iterator()

        while (var4.hasNext()) {
            val entry: Object2IntMap.Entry<ResourceLocation?> = var4.next() as Object2IntMap.Entry<*>
            level.getRecipeManager().byKey(entry.key).ifPresent(Consumer { p_300839_: RecipeHolder<*>? ->
                list.add(p_300839_)
                net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.createExperience(
                    level,
                    popVec,
                    entry.getIntValue(),
                    (p_300839_!!.value() as AbstractCookingRecipe).getExperience()
                )
            })
        }

        return list
    }

    override fun fillStackedContents(helper: StackedContents) {
        for (itemstack in this.items) {
            helper.accountStack(itemstack)
        }
    }

    companion object {
        protected const val SLOT_INPUT: Int = 0
        protected const val SLOT_FUEL: Int = 1
        protected const val SLOT_RESULT: Int = 2
        const val DATA_LIT_TIME: Int = 0
        private val SLOTS_FOR_UP = intArrayOf(0)
        private val SLOTS_FOR_DOWN = intArrayOf(2, 1)
        private val SLOTS_FOR_SIDES = intArrayOf(1)
        const val DATA_LIT_DURATION: Int = 1
        const val DATA_COOKING_PROGRESS: Int = 2
        const val DATA_COOKING_TOTAL_TIME: Int = 3
        const val NUM_DATA_VALUES: Int = 4
        const val BURN_TIME_STANDARD: Int = 200
        const val BURN_COOL_SPEED: Int = 2

        @Volatile
        private var fuelCache: MutableMap<Item?, Int?>? = null
        fun invalidateCache() {
            net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.fuelCache = null
        }

        val fuel: MutableMap<Item?, Int?>
            get() {
                val map: MutableMap<Item?, Int?>? =
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.fuelCache
                if (map != null) {
                    return map
                } else {
                    val map1: MutableMap<Item?, Int?> = Maps.newLinkedHashMap<Item?, Int?>()
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.LAVA_BUCKET,
                        20000
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.COAL_BLOCK,
                        16000
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.BLAZE_ROD,
                        2400
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.COAL,
                        1600
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.CHARCOAL,
                        1600
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.LOGS,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.BAMBOO_BLOCKS,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.PLANKS,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.BAMBOO_MOSAIC,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOODEN_STAIRS,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.BAMBOO_MOSAIC_STAIRS,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOODEN_SLABS,
                        150
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.BAMBOO_MOSAIC_SLAB,
                        150
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOODEN_TRAPDOORS,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOODEN_PRESSURE_PLATES,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOODEN_FENCES,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.FENCE_GATES,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.NOTE_BLOCK,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.BOOKSHELF,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.CHISELED_BOOKSHELF,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.LECTERN,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.JUKEBOX,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.CHEST,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.TRAPPED_CHEST,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.CRAFTING_TABLE,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.DAYLIGHT_DETECTOR,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.BANNERS,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.BOW,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.FISHING_ROD,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.LADDER,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.SIGNS,
                        200
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.HANGING_SIGNS,
                        800
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.WOODEN_SHOVEL,
                        200
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.WOODEN_SWORD,
                        200
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.WOODEN_HOE,
                        200
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.WOODEN_AXE,
                        200
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.WOODEN_PICKAXE,
                        200
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOODEN_DOORS,
                        200
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.BOATS,
                        1200
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOOL,
                        100
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOODEN_BUTTONS,
                        100
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.STICK,
                        100
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.SAPLINGS,
                        100
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.BOWL,
                        100
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        ItemTags.WOOL_CARPETS,
                        67
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.DRIED_KELP_BLOCK,
                        4001
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Items.CROSSBOW,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.BAMBOO,
                        50
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.DEAD_BUSH,
                        100
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.SCAFFOLDING,
                        50
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.LOOM,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.BARREL,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.CARTOGRAPHY_TABLE,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.FLETCHING_TABLE,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.SMITHING_TABLE,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.COMPOSTER,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.AZALEA,
                        100
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.FLOWERING_AZALEA,
                        100
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.add(
                        map1,
                        Blocks.MANGROVE_ROOTS,
                        300
                    )
                    net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.fuelCache = map1
                    return map1
                }
            }

        private fun isNeverAFurnaceFuel(item: Item): Boolean {
            return item.builtInRegistryHolder().`is`(ItemTags.NON_FLAMMABLE_WOOD)
        }

        private fun add(map: MutableMap<Item?, Int?>, itemTag: TagKey<Item?>, burnTime: Int) {
            for (holder in BuiltInRegistries.ITEM.getTagOrEmpty(itemTag)) {
                if (!net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.isNeverAFurnaceFuel(
                        holder.value() as Item
                    )
                ) {
                    map.put(holder.value() as Item, burnTime)
                }
            }
        }

        private fun add(map: MutableMap<Item?, Int?>, p_item: ItemLike, burnTime: Int) {
            val item = p_item.asItem()
            if (net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.isNeverAFurnaceFuel(item)) {
                if (SharedConstants.IS_RUNNING_IN_IDE) {
                    throw Util.pauseInIde<IllegalStateException?>(
                        IllegalStateException(
                            "A developer tried to explicitly make fire resistant item " + item.getName(
                                null as ItemStack?
                            ).getString() + " a furnace fuel. That will not work!"
                        )
                    ) as IllegalStateException
                }
            } else {
                map.put(item, burnTime)
            }
        }

        fun serverTick(
            level: Level,
            pos: BlockPos,
            state: BlockState,
            blockEntity: net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
        ) {
            var state = state
            val flag = blockEntity.isLit()
            var flag1 = false
            if (blockEntity.isLit()) {
                --blockEntity.litTime
            }

            val itemstack = blockEntity.items.get(1)
            val itemstack1 = blockEntity.items.get(0)
            val flag2 = !itemstack1.isEmpty()
            val flag3 = !itemstack.isEmpty()
            if (blockEntity.isLit() || flag3 && flag2) {
                val recipeholder: RecipeHolder<*>?
                if (flag2) {
                    recipeholder = blockEntity.quickCheck.getRecipeFor(SingleRecipeInput(itemstack1), level)
                        .orElse(null as Any?) as RecipeHolder<*>?
                } else {
                    recipeholder = null
                }

                val i = blockEntity.getMaxStackSize()
                if (!blockEntity.isLit() && net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.canBurn(
                        level.registryAccess(),
                        recipeholder,
                        blockEntity.items,
                        i
                    )
                ) {
                    blockEntity.litTime = blockEntity.getBurnDuration(itemstack)
                    blockEntity.litDuration = blockEntity.litTime
                    if (blockEntity.isLit()) {
                        flag1 = true
                        if (flag3) {
                            val item = itemstack.getItem()
                            itemstack.shrink(1)
                            if (itemstack.isEmpty()) {
                                val item1 = item.getCraftingRemainingItem()
                                blockEntity.items.set(1, if (item1 == null) ItemStack.EMPTY else ItemStack(item1))
                            }
                        }
                    }
                }

                if (blockEntity.isLit() && net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.canBurn(
                        level.registryAccess(),
                        recipeholder,
                        blockEntity.items,
                        i
                    )
                ) {
                    ++blockEntity.cookingProgress
                    if (blockEntity.cookingProgress == blockEntity.cookingTotalTime) {
                        blockEntity.cookingProgress = 0
                        blockEntity.cookingTotalTime =
                            net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.getTotalCookTime(
                                level,
                                blockEntity
                            )
                        if (net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.burn(
                                level.registryAccess(),
                                recipeholder,
                                blockEntity.items,
                                i
                            )
                        ) {
                            blockEntity.setRecipeUsed(recipeholder)
                        }

                        flag1 = true
                    }
                } else {
                    blockEntity.cookingProgress = 0
                }
            } else if (!blockEntity.isLit() && blockEntity.cookingProgress > 0) {
                blockEntity.cookingProgress =
                    Mth.clamp(blockEntity.cookingProgress - 2, 0, blockEntity.cookingTotalTime)
            }

            if (flag != blockEntity.isLit()) {
                flag1 = true
                state = state.setValue<Boolean?, Boolean?>(AbstractFurnaceBlock.LIT, blockEntity.isLit()) as BlockState
                level.setBlock(pos, state, 3)
            }

            if (flag1) {
                setChanged(level, pos, state)
            }
        }

        private fun canBurn(
            registryAccess: RegistryAccess,
            recipe: RecipeHolder<*>?,
            inventory: NonNullList<ItemStack>,
            maxStackSize: Int
        ): Boolean {
            if (!inventory.get(0).isEmpty() && recipe != null) {
                val itemstack = recipe.value().getResultItem(registryAccess)
                if (itemstack.isEmpty()) {
                    return false
                } else {
                    val itemstack1 = inventory.get(2)
                    if (itemstack1.isEmpty()) {
                        return true
                    } else if (!ItemStack.isSameItemSameComponents(itemstack1, itemstack)) {
                        return false
                    } else {
                        return if (itemstack1.getCount() < maxStackSize && itemstack1.getCount() < itemstack1.getMaxStackSize()) true else itemstack1.getCount() < itemstack.getMaxStackSize()
                    }
                }
            } else {
                return false
            }
        }

        private fun burn(
            registryAccess: RegistryAccess,
            recipe: RecipeHolder<*>?,
            inventory: NonNullList<ItemStack>,
            maxStackSize: Int
        ): Boolean {
            if (recipe != null && net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.canBurn(
                    registryAccess,
                    recipe,
                    inventory,
                    maxStackSize
                )
            ) {
                val itemstack = inventory.get(0)
                val itemstack1 = recipe.value().getResultItem(registryAccess)
                val itemstack2 = inventory.get(2)
                if (itemstack2.isEmpty()) {
                    inventory.set(2, itemstack1.copy())
                } else if (ItemStack.isSameItemSameComponents(itemstack2, itemstack1)) {
                    itemstack2.grow(1)
                }

                if (itemstack.`is`(Blocks.WET_SPONGE.asItem()) && !inventory.get(1).isEmpty() && inventory.get(1)
                        .`is`(Items.BUCKET)
                ) {
                    inventory.set(1, ItemStack(Items.WATER_BUCKET))
                }

                itemstack.shrink(1)
                return true
            } else {
                return false
            }
        }

        private fun getTotalCookTime(
            level: Level,
            blockEntity: net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
        ): Int {
            val singlerecipeinput = SingleRecipeInput(blockEntity.getItem(0))
            return blockEntity.quickCheck.getRecipeFor(singlerecipeinput, level)
                .map<Int?>(Function { p_300840_: RecipeHolder<AbstractCookingRecipe?>? -> (p_300840_!!.value() as AbstractCookingRecipe).getCookingTime() })
                .orElse(200) as Int
        }

        fun isFuel(stack: ItemStack): Boolean {
            return net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.Companion.getFuel()
                .containsKey(stack.getItem())
        }

        private fun createExperience(level: ServerLevel, popVec: Vec3, recipeIndex: Int, experience: Float) {
            var i = Mth.floor(recipeIndex.toFloat() * experience)
            val f = Mth.frac(recipeIndex.toFloat() * experience)
            if (f != 0.0f && Math.random() < f.toDouble()) {
                ++i
            }

            ExperienceOrb.award(level, popVec, i)
        }
    }
}