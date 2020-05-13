/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;

/**
 * @author Robijnvogel
 */
public class GUIRiftConfigScreen extends GUIConfigScreen {

    private InventoryPlayer playerInv;
    private TileEntityRift rift;

    public GUIRiftConfigScreen(Object Rift, InventoryPlayer playerInv) {
        super();
        this.rift = (TileEntityRift) Rift;
        this.playerInv = playerInv;
    }

    @Override
    protected void drawGUIConfigScreenForeground(int mouseX, int mouseY) {
        String name = I18n.format(ModBlocks.RIFT + ".name");
        String color = rift.getColor().toString();
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString(color, xSize / 2 - fontRenderer.getStringWidth(color) / 2, 12, 0x404040);
        fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
    }

    @Override
    public void drawGUIConfigScreenBackground(float partialTicks, int mouseX, int mouseY) {
        //TODO : make this value change based on the number of settings in the GUI
        int nrOfLines = 20;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(BG_TEXTURE);
        int x = this.guiLeft;
        int y = this.guiTop;
        drawTexturedModalRect(x, y, 0, 0, xSize, 4);
        int i = 1;
        for (; i < nrOfLines; i++) {
            drawTexturedModalRect(x, y + 4 * i, 0, 4, xSize, 4);
        }
        drawTexturedModalRect(x, y + 4 * i, 0, ySize - 4, xSize, 4);
    }

}
