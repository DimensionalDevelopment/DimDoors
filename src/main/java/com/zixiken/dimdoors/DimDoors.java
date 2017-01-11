package com.zixiken.dimdoors;

import com.zixiken.dimdoors.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = DimDoors.MODID, name = "Dimensional Doors", version = DimDoors.VERSION)
public class DimDoors {
	public static final String VERSION = "3.0.0-a1";
	public static final String MODID = "dimdoors";

	@SidedProxy(clientSide = "com.zixiken.dimdoors.client.ClientProxy",
            serverSide = "com.zixiken.dimdoors.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance(DimDoors.MODID)
	public static DimDoors instance;
	
	public static CreativeTabs dimDoorsCreativeTab = new CreativeTabs("dimDoorsCreativeTab") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {return ModItems.itemDimDoor;}
	};

	@Mod.EventHandler
	public void onPreInitialization(FMLPreInitializationEvent event) {
		proxy.onPreInitialization(event);
	}

	@Mod.EventHandler
	public void onInitialization(FMLInitializationEvent event) {
        proxy.onInitialization(event);
	}
}
