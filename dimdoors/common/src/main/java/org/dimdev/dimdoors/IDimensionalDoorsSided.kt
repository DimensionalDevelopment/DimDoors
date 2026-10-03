package org.dimdev.dimdoors

import net.minecraft.server.MinecraftServer
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.RecipeBookType
import net.minecraft.world.item.Item
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.ISided
import org.dimdev.dimdoors.compat.sable.SableLevelSpaceHelper
import org.dimdev.dimdoors.fluid.EternalFluid
import org.dimdev.dimdoors.fluid.LeakFluid
import org.dimdev.dimdoors.item.FarShotItem

interface IDimensionalDoorsSided<T : IDimensionalDoorsSided<T>> : ISided<T> {
    val tesselatingRecipeBookType: RecipeBookType

    fun checkCompat() {
        if (platform.isModLoaded("sable")) {
            SableLevelSpaceHelper.init()
        }
    }

    fun onServerStopped(server: (MinecraftServer) -> Unit)

    fun onServerStopping(server: (MinecraftServer) -> Unit)

    fun createFlowingEternalFluid(): Fluid {
        return EternalFluid.Flowing()
    }

    fun createEternalFluid(): FlowingFluid {
        return EternalFluid.Still()
    }

    fun createFlowingLeakFluid(): Fluid {
        return LeakFluid.Flowing()
    }

    fun createLeakFluid(): FlowingFluid {
        return LeakFluid.Still()
    }

    fun registerGameRule(name: String, category: GameRules.Category, value: Boolean): GameRules.Key<GameRules.BooleanValue>

    fun registerGameRule(name: String, category: GameRules.Category, value: Int): GameRules.Key<GameRules.IntegerValue>

    fun createFarShot(properties: Item.Properties): FarShotItem

    val enderPearlsTag: TagKey<Item>
}
