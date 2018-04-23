package org.dimdev.vanillafix.mixins;

import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("unused") // Shadow
@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer implements INetHandlerPlayServer {

    @Shadow public EntityPlayerMP player;
    @Shadow private /*final*/ MinecraftServer server;
    @Shadow private int networkTickCount;
    @Shadow private double firstGoodX;
    @Shadow private double firstGoodY;
    @Shadow private double firstGoodZ;
    @Shadow private double lastGoodX;
    @Shadow private double lastGoodY;
    @Shadow private double lastGoodZ;
    @Shadow private int lastPositionUpdate;
    @Shadow private boolean floating;
    @Shadow private Vec3d targetPos;
    @Shadow private static final Logger LOGGER = null;
    @Shadow private int movePacketCounter;
    @Shadow private int lastMovePacketCounter;

    @Shadow public void disconnect(final ITextComponent textComponent) {}

    @Shadow private static boolean isMovePlayerPacketInvalid(CPacketPlayer packetIn) { return false; }

    @Shadow private void captureCurrentPosition() {}

    @Shadow public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {}

    @Overwrite
    @Override
    public void processPlayer(CPacketPlayer packet) {
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, player.getServerWorld());

        if (isMovePlayerPacketInvalid(packet)) {
            disconnect(new TextComponentTranslation("multiplayer.disconnect.invalid_player_movement"));
        } else {
            WorldServer world = server.getWorld(player.dimension);

            if (player.queuedEndExit) return;

            if (networkTickCount == 0) {
                captureCurrentPosition();
            }

            if (targetPos != null) {
                if (networkTickCount - lastPositionUpdate > 20) {
                    lastPositionUpdate = networkTickCount;
                    setPlayerLocation(targetPos.x, targetPos.y, targetPos.z, player.rotationYaw, player.rotationPitch);
                }
            } else {
                lastPositionUpdate = networkTickCount;

                if (player.isRiding()) {
                    player.setPositionAndRotation(player.posX, player.posY, player.posZ, packet.getYaw(player.rotationYaw), packet.getPitch(player.rotationPitch));
                    server.getPlayerList().serverUpdateMovingPlayer(player);
                } else {
                    double oldX = player.posX;
                    double oldY = player.posY;
                    double oldZ = player.posZ;
                    double oldY2 = player.posY;

                    double packetX = packet.getX(player.posX);
                    double packetY = packet.getY(player.posY);
                    double packetZ = packet.getZ(player.posZ);
                    float packetYaw = packet.getYaw(player.rotationYaw);
                    float packetPitch = packet.getPitch(player.rotationPitch);

                    double xDiff = packetX - firstGoodX;
                    double yDiff = packetY - firstGoodY;
                    double zDiff = packetZ - firstGoodZ;
                    double speedSq = player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ;
                    double distanceSq = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;

                    if (player.isPlayerSleeping()) {
                        if (distanceSq > 1.0D) {
                            setPlayerLocation(player.posX, player.posY, player.posZ, packet.getYaw(player.rotationYaw), packet.getPitch(player.rotationPitch));
                        }
                    } else {
                        ++movePacketCounter;
                        int packetCount = movePacketCounter - lastMovePacketCounter;

                        if (packetCount > 5) {
                            LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", player.getName(), packetCount);
                            packetCount = 1;
                        }

                        if (!player.isInvulnerableDimensionChange() && (!player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !player.isElytraFlying())) {
                            float maxDistancePerTic = player.isElytraFlying() ? 300.0F : 100.0F;

                            if (distanceSq - speedSq > maxDistancePerTic * packetCount && (!server.isSinglePlayer() || !server.getServerOwner().equals(player.getName()))) {
                                LOGGER.warn("{} moved too quickly! {},{},{}", player.getName(), xDiff, yDiff, zDiff);
                                setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                                return;
                            }
                        }

                        boolean notInsideBlock = world.getCollisionBoxes(player, player.getEntityBoundingBox().shrink(0.0625D)).isEmpty();
                        xDiff = packetX - lastGoodX;
                        yDiff = packetY - lastGoodY;
                        zDiff = packetZ - lastGoodZ;

                        if (player.onGround && !packet.isOnGround() && yDiff > 0.0D) {
                            player.jump();
                        }

                        player.move(MoverType.PLAYER, xDiff, yDiff, zDiff);
                        player.onGround = packet.isOnGround();
                        double oldYDiff = yDiff;

                        xDiff = packetX - player.posX;
                        yDiff = packetY - player.posY;
                        if (yDiff > -0.5D || yDiff < 0.5D) { // TODO: But why?
                            yDiff = 0.0D;
                        }
                        zDiff = packetZ - player.posZ;
                        distanceSq = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;

                        boolean movedWrongly = false;
                        if (!player.isInvulnerableDimensionChange() && distanceSq > 0.0625D && !player.isPlayerSleeping() && !player.interactionManager.isCreative() && player.interactionManager.getGameType() != GameType.SPECTATOR) {
                            movedWrongly = true;
                            LOGGER.warn("{} moved wrongly!", player.getName());
                        }

                        // Fix https://bugs.mojang.com/browse/MC-98153
                        //player.setPositionAndRotation(packetX, packetY, packetZ, packetYaw, packetPitch);
                        //player.addMovementStat(player.posX - oldX, player.posY - oldY, player.posZ - oldZ);
                        player.addMovementStat(packetX - oldX, packetY - oldY, packetZ - oldZ);

                        // Fix https://bugs.mojang.com/browse/MC-123364 (partially, players can still cheat to teleport into a portal)
                        if (!player.isInvulnerableDimensionChange() && !player.noClip && !player.isPlayerSleeping()) {
                            boolean oldPositionEmpty = world.getCollisionBoxes(player, player.getEntityBoundingBox().shrink(0.0625D)).isEmpty();

                            if (notInsideBlock && (movedWrongly || !oldPositionEmpty)) {
                                setPlayerLocation(oldX, oldY, oldZ, packetYaw, packetPitch);
                                return;
                            }
                        }

                        floating = oldYDiff >= -0.03125D;
                        floating &= !server.isFlightAllowed() && !player.capabilities.allowFlying;
                        floating &= !player.isPotionActive(MobEffects.LEVITATION) && !player.isElytraFlying() && !world.checkBlockCollision(player.getEntityBoundingBox().grow(0.0625D).expand(0.0D, -0.55D, 0.0D));
                        player.onGround = packet.isOnGround();
                        server.getPlayerList().serverUpdateMovingPlayer(player);
                        player.handleFalling(player.posY - oldY2, packet.isOnGround());
                        lastGoodX = player.posX;
                        lastGoodY = player.posY;
                        lastGoodZ = player.posZ;
                    }
                }
            }
        }
    }
}
