package org.dimdev.dimdoors.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
    public static final ItemGroup DIMENSIONAL_DOORS = FabricItemGroupBuilder
            .create(new Identifier("dimdoors", "dimensional_doors"))
            .icon(() -> new ItemStack(ModItems.IRON_DIMENSIONAL_DOOR))
            .build();
}
