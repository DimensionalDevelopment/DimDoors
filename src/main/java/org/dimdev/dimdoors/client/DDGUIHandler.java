package org.dimdev.dimdoors.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 *
 * @author Robijnvogel
 */
public class DDGUIHandler implements IGuiHandler {
    //TODO: move this to a non-client package?

    public static final int RIFT = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return ID==RIFT ? world.getTileEntity(new BlockPos(x, y, z)) : null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return ID==RIFT ? new GUIRiftConfigScreen(getServerGuiElement(ID, player, world, x, y, z), player.inventory) : null;
    }
}
