/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;

/**
 *
 * @author Robijnvogel
 */
public class GUIRiftConfigScreen extends GUIConfigScreen {

    private final InventoryPlayer playerInv;
    private final TileEntityRift rift;
      
    public GUIRiftConfigScreen(Object Rift, InventoryPlayer playerInv) {
        super();
        this.rift = (TileEntityRift) Rift;
        this.playerInv = playerInv;
    }

    @Override
    protected void drawGUIConfigScreenForeground(int mouseX, int mouseY) {
        String name = I18n.format(ModBlocks.RIFT + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
    }

    @Override
    public void drawGUIConfigScreenBackground(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(BG_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

}
