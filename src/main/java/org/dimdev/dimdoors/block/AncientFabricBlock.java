package org.dimdev.dimdoors.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class AncientFabricBlock extends Block {
	public AncientFabricBlock(DyeColor color) {
		super(FabricBlockSettings.of(Material.STONE, color).strength(-1.0F, 3600000.0F).noLootTable());
	}
}
