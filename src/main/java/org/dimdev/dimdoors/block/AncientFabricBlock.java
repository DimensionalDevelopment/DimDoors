package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.DyeColor;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

public class AncientFabricBlock extends Block {
	public AncientFabricBlock(DyeColor color) {
		super(FabricBlockSettings.of(Material.STONE, color).strength(-1.0F, 3600000.0F).dropsNothing());
	}
}
