package com.zixiken.dimdoors.items;

import com.flowpowered.math.vector.Vector3d;
import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.blocks.ModBlocks;
import com.zixiken.dimdoors.tileentities.TileEntityRift;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static com.zixiken.dimdoors.DimDoors.translateAndAdd;

public class ItemLinkSignature extends Item {
    public static final String ID = "itemLinkSignature";

    public ItemLinkSignature() {
        super();
        setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setUnlocalizedName(ID);
        setRegistryName(ID);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        translateAndAdd("info.riftSignature.unbound", list);
    }
}
