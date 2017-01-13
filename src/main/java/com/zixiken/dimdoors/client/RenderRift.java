package com.zixiken.dimdoors.client;

import com.zixiken.dimdoors.tileentities.TileEntityRift;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class RenderRift extends TileEntitySpecialRenderer<TileEntityRift> {

    public void renderTileEntityAt(TileEntityRift te, double x, double y, double z, float partialTicks, int destroyStage) {
        ITextComponent itextcomponent = new TextComponentString("Derp");

        this.setLightmapDisabled(true);
        this.drawNameplate(te, itextcomponent.getFormattedText(), x, y, z, 12);
        this.setLightmapDisabled(false);
    }
}
