package org.dimdev.dimdoors.block.entity;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.targets.MessageTarget;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

public abstract class RiftBlockEntity extends BlockEntity implements Target, EntityTarget {
	private static final Logger LOGGER = LogManager.getLogger();
	public static long showRiftCoreUntil = 0;

	@NotNull
	protected RiftData data = new RiftData();

	protected boolean riftStateChanged;

	public RiftBlockEntity(BlockEntityType<? extends RiftBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.deserialize(nbt);
	}

	public void deserialize(CompoundTag nbt) {
		this.data = RiftData.fromNbt(nbt.getCompound("data"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		this.serialize(nbt);
	}

	public CompoundTag serialize(CompoundTag nbt) {
		nbt.put("data", RiftData.toNbt(this.data));
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public void setDestination(VirtualTarget destination) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Setting destination {} for {}", destination, this.worldPosition.toShortString());
		}

		if (this.getDestination() != null && this.isRegistered()) {
			this.getDestination().unregister();
		}
		this.data.setDestination(destination);
		if (destination != null) {
			if (this.level != null && this.worldPosition != null) {
				destination.setLocation(new Location((ServerWorld) this.level, this.worldPosition));
			}
			if (this.isRegistered()) destination.register();
		}
		this.riftStateChanged = true;
		this.markDirty();
		this.updateColor();
	}

	public void setColor(RGBA color) {
		this.data.setColor(color);
		this.markDirty();
	}

	public void setProperties(LinkProperties properties) {
		this.data.setProperties(properties);
		this.updateProperties();
		this.markDirty();
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.tracking(this)) {
			ModCriteria.RIFT_TRACKED.trigger(serverPlayerEntity);
		}
		return super.toInitialChunkDataNbt();
	}

	public void markStateChanged() {
		this.riftStateChanged = true;
		this.markDirty();
	}

	public boolean isRegistered() { // TODO: do we need to implement this for v2?
		return /*!PocketTemplate.isReplacingPlaceholders() &&*/ this.level != null && DimensionalRegistry.getRiftRegistry().isRiftAt(new Location((ServerWorld) this.level, this.worldPosition));
	}

	public void register() {
		if (this.isRegistered()) {
			return;
		}

		Location loc = new Location((ServerWorld) this.level, this.worldPosition);
		DimensionalRegistry.getRiftRegistry().addRift(loc);
		if (this.data.getDestination() != VirtualTarget.NoneTarget.INSTANCE) this.data.getDestination().register();
		this.updateProperties();
		this.updateColor();
	}

	public void updateProperties() {
		if (this.isRegistered())
			DimensionalRegistry.getRiftRegistry().setProperties(new Location((ServerWorld) this.level, this.worldPosition), this.data.getProperties());
		this.markDirty();
	}

	public void unregister() {
		if (this.isRegistered()) {
			DimensionalRegistry.getRiftRegistry().removeRift(new Location((ServerWorld) this.level, this.worldPosition));
		}
	}

	public void updateType() {
		if (!this.isRegistered()) return;
		Rift rift = DimensionalRegistry.getRiftRegistry().getRift(new Location((ServerWorld) this.level, this.worldPosition));
		rift.setDetached(this.isDetached());
		rift.markDirty();
	}

	public void handleTargetGone(Location location) {
		if (this.data.getDestination().shouldInvalidate(location)) {
			this.data.setDestination(VirtualTarget.NoneTarget.INSTANCE);
			this.markDirty();
		}

		this.updateColor();
	}

	public void handleSourceGone(Location location) {
		this.updateColor();
	}

	public Target getTarget() {
		if (this.data.getDestination() == VirtualTarget.NoneTarget.INSTANCE) {
			return new MessageTarget("rifts.unlinked1");
		} else {
			//noinspection ConstantConditions
			this.data.getDestination().setLocation(new Location((ServerWorld) this.level, this.worldPosition));
			return this.data.getDestination();
		}
	}

	public boolean teleport(Entity entity) {
		this.riftStateChanged = false;

		// Attempt a teleport
		try {
			Vec3 relativePos = new Vec3(0, 0, 0);
			Rotations relativeAngle = new Rotations(entity.getXRot(), entity.getYRot(), 0);
			Vec3 relativeVelocity = entity.getDeltaMovement();
			EntityTarget target = this.getTarget().as(Targets.ENTITY);

			BlockState state = this.getLevel().getBlockState(this.getBlockPos());
			Block block = state.getBlock();
			if (block instanceof CoordinateTransformerBlock) {
				CoordinateTransformerBlock transformer = (CoordinateTransformerBlock) block;
				TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformer.transformationBuilder(state, this.getPos());
				TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = transformer.rotatorBuilder(state, this.getPos());
				relativePos = transformer.transformTo(transformationBuilder, entity.getPos());
				relativeAngle = transformer.rotateTo(rotatorBuilder, relativeAngle);
				relativeVelocity = transformer.rotateTo(rotatorBuilder, relativeVelocity);
			}

			if (target.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity)) {
				VirtualLocation vLoc = VirtualLocation.fromLocation(new Location((ServerWorld) entity.level, entity.getBlockPos()));
				EntityUtils.chat(entity, Text.of("You are at x = " + vLoc.getX() + ", y = ?, z = " + vLoc.getZ() + ", w = " + vLoc.getDepth()));
				return true;
			}
		} catch (Exception e) {
			EntityUtils.chat(entity, Text.of("Something went wrong while trying to teleport you, please report this bug."));
			LOGGER.error("Teleporting failed with the following exception: ", e);
		}

		return false;
	}

	public void updateColor() {
		if (this.data.isForcedColor()) return;
		if (!this.isRegistered()) {
			this.data.setColor(new RGBA(0, 0, 0, 1));
		} else if (this.data.getDestination() == VirtualTarget.NoneTarget.INSTANCE) {
			this.data.setColor(new RGBA(0.7f, 0.7f, 0.7f, 1));
		} else {
			this.data.getDestination().setLocation(new Location((ServerWorld) this.level, this.worldPosition));
			RGBA newColor = this.data.getDestination().getColor();
			if (this.data.getColor() == null && newColor != null || !Objects.equals(this.data.getColor(), newColor)) {
				this.data.setColor(newColor);
				this.markDirty();
			}
		}
	}

	public abstract boolean isDetached();

	public abstract void setLocked(boolean locked);

	public abstract boolean isLocked();

	public void copyFrom(DetachedRiftBlockEntity rift) {
		this.data.setDestination(rift.data.getDestination());
		this.data.setProperties(rift.data.getProperties());
		this.data.setAlwaysDelete(rift.data.isAlwaysDelete());
		this.data.setForcedColor(rift.data.isForcedColor());
	}

	public VirtualTarget getDestination() {
		return this.data.getDestination();
	}

	public LinkProperties getProperties() {
		return this.data.getProperties();
	}

	public boolean isAlwaysDelete() {
		return this.data.isAlwaysDelete();
	}

	public boolean isForcedColor() {
		return this.data.isForcedColor();
	}

	public RGBA getColor() {
		return this.data.getColor();
	}

	public void setData(RiftData data) {
		this.data = data;
	}

	public RiftData getData() {
		return this.data;
	}

	public void setWorld(Level level) {
		this.level = level;
	}

	public Rift asRift() {
		return DimensionalRegistry.getRiftRegistry().getRift(new Location(this.level.dimension(), this.worldPosition));
	}
}
