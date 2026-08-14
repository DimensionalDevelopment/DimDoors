package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.block.DetachedRiftBlock;
import org.dimdev.dimdoors.client.effect.LevelRendererExtension;
import org.joml.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.renderer.LevelRenderer.getLightColor;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements LevelRendererExtension {
    @Shadow
    private int prevCloudX;

    @Shadow
    private int prevCloudY;

    @Shadow
    private int prevCloudZ;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Nullable
    private CloudStatus prevCloudsType;

    @Shadow
    private Vec3 prevCloudColor;

    @Shadow
    private boolean generateClouds;

    @Shadow
    @Nullable
    private VertexBuffer cloudBuffer;

    @Shadow
    protected abstract MeshData buildClouds(Tesselator arg, double d, double e, double g, Vec3 arg2);

    @Shadow
    @Final
    private static ResourceLocation RAIN_LOCATION;

    @Shadow
    @Final
    private float[] rainSizeZ;

    @Shadow
    @Final
    private float[] rainSizeX;

    @Shadow
    @Final
    private static ResourceLocation SNOW_LOCATION;

    @Shadow
    protected abstract boolean doesMobEffectBlockSky(Camera arg);

    @Shadow
    private int ticks;

    public void renderCloudBuffer(PoseStack poseStack, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick, int ticks, double camX, double camY, double camZ, float cloudHeight, Vec3 cloudColor) {
        if (Float.isNaN(cloudHeight)) {
            return;
        }

        final float CLOUD_SCALE = 12.0F;
        final float CLOUD_HEIGHT_SCALE = 4.0F;

        double cloudTime = (ticks + partialTick) * 0.03;
        double cloudTexU = (camX + cloudTime) / CLOUD_SCALE;
        double cloudTexV = (cloudHeight - camY + 0.33) / CLOUD_HEIGHT_SCALE;
        double cloudTexVOffset = camZ / CLOUD_SCALE + 0.33;

        // Wrap texture coordinates every 2048 units to prevent precision issues
        cloudTexU -= Mth.floor(cloudTexU / 2048.0) * 2048.0;
        cloudTexVOffset -= Mth.floor(cloudTexVOffset / 2048.0) * 2048.0;

        float fracU = (float) (cloudTexU - Mth.floor(cloudTexU));
        float fracV = (float) (cloudTexVOffset - Mth.floor(cloudTexVOffset));
        float cloudYFrac = (float) (cloudTexV - Mth.floor(cloudTexV / CLOUD_HEIGHT_SCALE)) * CLOUD_HEIGHT_SCALE;

        int cloudXFloor = (int) Math.floor(cloudTexU);
        int cloudYFloor = (int) Math.floor(cloudTexV / CLOUD_HEIGHT_SCALE);
        int cloudZFloor = (int) Math.floor(cloudTexVOffset);

        boolean needsRegenerate =
                cloudXFloor != this.prevCloudX ||
                        cloudYFloor != this.prevCloudY ||
                        cloudZFloor != this.prevCloudZ ||
                        this.minecraft.options.getCloudsType() != this.prevCloudsType ||
                        this.prevCloudColor.distanceToSqr(cloudColor) > 2.0E-4;

        if (needsRegenerate) {
            this.prevCloudX = cloudXFloor;
            this.prevCloudY = cloudYFloor;
            this.prevCloudZ = cloudZFloor;
            this.prevCloudColor = cloudColor;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
        }

        if (this.generateClouds) {
            this.generateClouds = false;

            if (this.cloudBuffer != null) {
                this.cloudBuffer.close();
            }

            this.cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            this.cloudBuffer.bind();
            this.cloudBuffer.upload(
                    this.buildClouds(
                            Tesselator.getInstance(),
                            cloudTexU, cloudTexV, cloudTexVOffset,
                            cloudColor
                    )
            );
            VertexBuffer.unbind();
        }

        FogRenderer.levelFogColor();

        poseStack.pushPose();
        poseStack.mulPose(modelViewMatrix);
        poseStack.scale(CLOUD_SCALE, 1.0F, CLOUD_SCALE);
        poseStack.translate(-fracU, cloudYFrac, -fracV);

        if (this.cloudBuffer != null) {
            this.cloudBuffer.bind();

            int startLayer = (this.prevCloudsType == CloudStatus.FANCY) ? 0 : 1;

            for (int layer = startLayer; layer < 2; layer++) {
                RenderType cloudLayer = (layer == 0) ? RenderType.cloudsDepthOnly() : RenderType.clouds();
                cloudLayer.setupRenderState();

                ShaderInstance shader = RenderSystem.getShader();
                this.cloudBuffer.drawWithShader(
                        poseStack.last().pose(),
                        projectionMatrix,
                        shader
                );

                cloudLayer.clearRenderState();
            }

            VertexBuffer.unbind();
        }

        poseStack.popPose();
    }

    public void renderWeather(LightTexture lightTexture, float partialTick, int ticks, double camX, double camY, double camZ, @NonnullDefault Biome.Precipitation precipitation, float rainStrength) {
        if (rainStrength <= 0.0F) {
            return;
        }

        lightTexture.turnOnLightLayer();

        Level level = this.minecraft.level;
        int centerX = Mth.floor(camX);
        int centerY = Mth.floor(camY);
        int centerZ = Mth.floor(camZ);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = null;

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        final int RAIN_RADIUS = Minecraft.useFancyGraphics() ? 10 : 5;

        RenderSystem.depthMask(Minecraft.useShaderTransparency());

        int currentTextureMode = -1;  // -1 = none, 0 = rain, 1 = snow
        float gameTime = (float) ticks + partialTick;

        RenderSystem.setShader(GameRenderer::getParticleShader);

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dz = centerZ - RAIN_RADIUS; dz <= centerZ + RAIN_RADIUS; ++dz) {
            for (int dx = centerX - RAIN_RADIUS; dx <= centerX + RAIN_RADIUS; ++dx) {
                int particleIndex = (dz - centerZ + 16) * 32 + (dx - centerX + 16);
                double windX = this.rainSizeX[particleIndex] * 0.5;
                double windZ = this.rainSizeZ[particleIndex] * 0.5;

                mutablePos.set(dx, camY, dz);

                if (precipitation == Biome.Precipitation.NONE) {
                    continue;
                }

                int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, dx, dz);
                int minY = Math.max(centerY - RAIN_RADIUS, groundY);
                int maxY = Math.min(centerY + RAIN_RADIUS, groundY + RAIN_RADIUS);

                if (minY >= maxY) {
                    continue;
                }

                RandomSource random = RandomSource.create(
                        (long) (dx * dx * 3121 + dx * 45238971 ^ dz * dz * 418711 + dz * 13761)
                );

                mutablePos.set(dx, minY, dz);

                //TODO: Potentially expand to other weatherTypes.
                switch (precipitation) {
                    case RAIN -> {
                        if (currentTextureMode != 0) {
                            if (currentTextureMode >= 0) {
                                BufferUploader.drawWithShader(buffer.buildOrThrow());
                            }
                            currentTextureMode = 0;
                            RenderSystem.setShaderTexture(0, RAIN_LOCATION);
                            buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        int tickOffset = ticks & 131071;
                        int posHash = (dx * dx * 3121 + dx * 45238971 + dz * dz * 418711 + dz * 13761) & 255;

                        float stretch = 3.0F + random.nextFloat();
                        float fallOffset = -((float) (tickOffset + posHash) + partialTick) / 32.0F * stretch;
                        float wrappedV = fallOffset % 32.0F;

                        double distX = (double) dx + 0.5 - camX;
                        double distZ = (double) dz + 0.5 - camZ;
                        float distanceFade = (float) Math.sqrt(distX * distX + distZ * distZ) / (float) RAIN_RADIUS;

                        float alpha = (1.0F - distanceFade * distanceFade) * 0.5F + 0.5F;
                        alpha *= rainStrength;

                        mutablePos.set(dx, groundY, dz);
                        int light = getLightColor(level, mutablePos);

                        float topY = (float) (maxY - camY);
                        float bottomY = (float) (minY - camY);

                        buffer.addVertex((float) (dx - camX - windX + 0.5), topY, (float) (dz - camZ - windZ + 0.5))
                                .setUv(0.0F, (float) minY * 0.25F + wrappedV)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setLight(light);

                        buffer.addVertex((float) (dx - camX + windX + 0.5), topY, (float) (dz - camZ + windZ + 0.5))
                                .setUv(1.0F, (float) minY * 0.25F + wrappedV)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setLight(light);

                        buffer.addVertex((float) (dx - camX + windX + 0.5), bottomY, (float) (dz - camZ + windZ + 0.5))
                                .setUv(1.0F, (float) maxY * 0.25F + wrappedV)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setLight(light);

                        buffer.addVertex((float) (dx - camX - windX + 0.5), bottomY, (float) (dz - camZ - windZ + 0.5))
                                .setUv(0.0F, (float) maxY * 0.25F + wrappedV)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setLight(light);
                    }
                    case SNOW -> {
                        if (currentTextureMode != 1) {
                            if (currentTextureMode >= 0) {
                                BufferUploader.drawWithShader(buffer.buildOrThrow());
                            }
                            currentTextureMode = 1;
                            RenderSystem.setShaderTexture(0, SNOW_LOCATION);
                            buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        float fallSpeed = -((float) (ticks & 511) + partialTick) / 512.0F;
                        float randomUOffset = (float) (random.nextDouble() + (double) gameTime * 0.01 * random.nextGaussian());
                        float randomVOffset = (float) (random.nextDouble() + (double) (gameTime * (float) random.nextGaussian()) * 0.001);

                        double distX = (double) dx + 0.5 - camX;
                        double distZ = (double) dz + 0.5 - camZ;
                        float distanceFade = (float) Math.sqrt(distX * distX + distZ * distZ) / (float) RAIN_RADIUS;

                        float alpha = ((1.0F - distanceFade * distanceFade) * 0.3F + 0.5F) * rainStrength;

                        mutablePos.set(dx, groundY, dz);
                        int light = getLightColor(level, mutablePos);
                        int lightBlock = light >> 16 & 0xFFFF;
                        int lightSky = light & 0xFFFF;
                        int packedLightU = (lightBlock * 3 + 240) / 4;
                        int packedLightV = (lightSky * 3 + 240) / 4;

                        float topY = (float) (maxY - camY);
                        float bottomY = (float) (minY - camY);

                        buffer.addVertex((float) (dx - camX - windX + 0.5), topY, (float) (dz - camZ - windZ + 0.5))
                                .setUv(0.0F + randomUOffset, (float) minY * 0.25F + fallSpeed + randomVOffset)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setUv2(packedLightU, packedLightV);

                        buffer.addVertex((float) (dx - camX + windX + 0.5), topY, (float) (dz - camZ + windZ + 0.5))
                                .setUv(1.0F + randomUOffset, (float) minY * 0.25F + fallSpeed + randomVOffset)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setUv2(packedLightU, packedLightV);

                        buffer.addVertex((float) (dx - camX + windX + 0.5), bottomY, (float) (dz - camZ + windZ + 0.5))
                                .setUv(1.0F + randomUOffset, (float) maxY * 0.25F + fallSpeed + randomVOffset)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setUv2(packedLightU, packedLightV);

                        buffer.addVertex((float) (dx - camX - windX + 0.5), bottomY, (float) (dz - camZ - windZ + 0.5))
                                .setUv(0.0F + randomUOffset, (float) maxY * 0.25F + fallSpeed + randomVOffset)
                                .setColor(1.0F, 1.0F, 1.0F, alpha)
                                .setUv2(packedLightU, packedLightV);
                    }
                }
            }
        }

        if (currentTextureMode >= 0) {
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        lightTexture.turnOffLightLayer();
    }

    @Override
    public boolean isMobEffectBlockingSky(Camera camera) {
        return this.doesMobEffectBlockSky(camera);
    }

    @Override
    public int getTicks() {
        return ticks;
    }

    @Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
    public void preventRiftOutline(PoseStack poseStack, VertexConsumer consumer, Entity entity,
                                   double camX, double camY, double camZ, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.getBlock() instanceof DetachedRiftBlock) {
            ci.cancel();
        }
    }
}
