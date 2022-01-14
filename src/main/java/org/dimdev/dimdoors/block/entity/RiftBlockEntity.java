package org.dimdev.dimdoors.block.entity;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.CoordinateTransformerBlock;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.rift.targets.MessageTarget;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

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
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.deserialize(nbt);
	}

	public void deserialize(NbtCompound nbt) {
		this.data = RiftData.fromNbt(nbt.getCompound("data"));
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		this.serialize(nbt);
	}

	public NbtCompound serialize(NbtCompound nbt) {
		nbt.put("data", RiftData.toNbt(this.data));
		return nbt;
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	public void setDestination(VirtualTarget destination) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Setting destination {} for {}", destination, this.pos.toShortString());
		}

		if (this.getDestination() != null && this.isRegistered()) {
			this.getDestination().unregister();
		}
		this.data.setDestination(destination);
		if (destination != null) {
			if (this.world != null && this.pos != null) {
				destination.setLocation(new Location((ServerWorld) this.world, this.pos));
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
		return /*!PocketTemplate.isReplacingPlaceholders() &&*/ this.world != null && DimensionalRegistry.getRiftRegistry().isRiftAt(new Location((ServerWorld) this.world, this.pos));
	}

	public void register() {
		if (this.isRegistered()) {
			return;
		}

		Location loc = new Location((ServerWorld) this.world, this.pos);
		DimensionalRegistry.getRiftRegistry().addRift(loc);
		if (this.data.getDestination() != VirtualTarget.NoneTarget.INSTANCE) this.data.getDestination().register();
		this.updateProperties();
		this.updateColor();
	}

	public void updateProperties() {
		if (this.isRegistered())
			DimensionalRegistry.getRiftRegistry().setProperties(new Location((ServerWorld) this.world, this.pos), this.data.getProperties());
		this.markDirty();
	}

	public void unregister() {
		if (this.isRegistered()) {
			DimensionalRegistry.getRiftRegistry().removeRift(new Location((ServerWorld) this.world, this.pos));
		}
	}

	public void updateType() {
		if (!this.isRegistered()) return;
		Rift rift = DimensionalRegistry.getRiftRegistry().getRift(new Location((ServerWorld) this.world, this.pos));
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
			this.data.getDestination().setLocation(new Location((ServerWorld) this.world, this.pos));
			return this.data.getDestination();
		}
	}

	public boolean teleport(Entity entity) {
		this.riftStateChanged = false;

		// Attempt a teleport
		try {
			Vec3d relativePos = new Vec3d(0, 0, 0);
			EulerAngle relativeAngle = new EulerAngle(entity.getPitch(), entity.getYaw(), 0);
			Vec3d relativeVelocity = entity.getVelocity();
			EntityTarget target = this.getTarget().as(Targets.ENTITY);

			BlockState state = this.getWorld().getBlockState(this.getPos());
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
				VirtualLocation vLoc = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, entity.getBlockPos()));
				EntityUtils.chat(entity, new LiteralText("You are at x = " + vLoc.getX() + ", y = ?, z = " + vLoc.getZ() + ", w = " + vLoc.getDepth()));
				return true;
			}
		} catch (Exception e) {
			EntityUtils.chat(entity, new LiteralText("Something went wrong while trying to teleport you, please report this bug."));
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
			this.data.getDestination().setLocation(new Location((ServerWorld) this.world, this.pos));
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

	public void setWorld(World world) {
		this.world = world;
	}

	public Rift asRift() {
		return DimensionalRegistry.getRiftRegistry().getRift(new Location(this.world.getRegistryKey(), this.pos));
	}
}
