package org.dimdev.dimdoors.world;

import org.dimdev.dimdoors.CloudRenderBlank;
import org.dimdev.dimdoors.config.DDProperties;
import org.dimdev.dimdoors.ticking.CustomLimboPopulator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

public class PersonalPocketProvider extends PocketProvider {
    private DDProperties properties;
    private CustomLimboPopulator spawner;
    private IRenderHandler skyRenderer;

    public PersonalPocketProvider() {
        super();
    }

    @Override
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
        setCloudRenderer(new CloudRenderBlank());
        return Vec3.createVectorHelper(1, 1, 1);
    }

    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    protected void generateLightBrightnessTable() {
        float f = 0.0F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;
            this.lightBrightnessTable[i] = (15);
        }
    }

    @Override
    public double getHorizon() {
        return worldObj.getHeight() - 256;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3 getFogColor(float par1, float par2) {
        return Vec3.createVectorHelper(1, 1, 1);
    }

    @Override
    public int getActualHeight() {
        return -256;
    }
}
