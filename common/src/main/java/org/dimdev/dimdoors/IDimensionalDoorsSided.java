package org.dimdev.dimdoors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.compat.sable.SableCompat;
import org.dimdev.dimdoors.fluid.EternalFluid;
import org.dimdev.dimdoors.fluid.LeakFluid;
import org.dimdev.limlib.api.ISided;

import java.util.function.Consumer;

public interface IDimensionalDoorsSided<T extends IDimensionalDoorsSided<T>> extends ISided<T> {
    public RecipeBookType getTesselatingRecipeBookType();

    default void checkCompat() {
        if(isModLoaded("sable")) {
            SableCompat.init();
        }
    }

    void onServerStopped(Consumer<MinecraftServer> server);

    void onServerStopping(Consumer<MinecraftServer> server);

    default Fluid createFlowingEternalFluid() {
        return new EternalFluid.Flowing();
    }

    default FlowingFluid createEternalFluid() {
        return new EternalFluid.Still();
    }

    default Fluid createFlowingLeakFluid() {
        return new LeakFluid.Flowing();
    }

    default FlowingFluid createLeakFluid() {
        return new LeakFluid.Still();
    }

    GameRules.Key<GameRules.BooleanValue> registerGameRule(String name, GameRules.Category category, boolean value);

    GameRules.Key<GameRules.IntegerValue> registerGameRule(String name, GameRules.Category category, int value);
}
