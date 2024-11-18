package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StringRepresentable;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.LocalReference;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Optional;

public class RelativeReferenceModifier extends AbstractModifier {
	public static final MapCodec<RelativeReferenceModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.INT.fieldOf("point_a").forGetter(a -> a.point_a),
			Codec.INT.fieldOf("point_b").forGetter(a -> a.point_b),
			StringRepresentable.fromValues(ConnectionType::values).fieldOf("connection").forGetter(a -> a.connection)
	).apply(instance, RelativeReferenceModifier::new));

	public static final String KEY = "relative";

	private int point_a;
    private int point_b;
	private ConnectionType connection = ConnectionType.BOTH;

	public RelativeReferenceModifier(int point_a, int point_b, ConnectionType connection) {
        this.point_a = point_a;
        this.point_b = point_b;
        this.connection = connection;
    }

	@Override
	public Modifier fromNbt(CompoundTag nbt, ResourceManager manager) {
		point_a = nbt.getInt("point_a");
		point_b = nbt.getInt("point_b");
		connection = nbt.contains("connection") ? ConnectionType.fromString(nbt.getString("connection")) : ConnectionType.BOTH;
		return this;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);
		nbt.putInt("point_a", point_a);
		nbt.putInt("point_b", point_b);
		nbt.putString("connection", connection.getSerializedName());
		return nbt;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.RELATIVE_REFERENCE_MODIFIER_TYPE.get();
	}

    @Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		Optional<Location> riftA = manager.get(point_a).map(rift -> new Location((ServerLevel) rift.getLevel(), rift.getBlockPos()));
		Optional<Location> riftB = manager.get(point_b).map(rift -> new Location((ServerLevel) rift.getLevel(), rift.getBlockPos()));

		if(riftA.isPresent() && riftB.isPresent()) {
			RiftReference link1 = LocalReference.tryMakeRelative(riftA.get(), riftB.get());
			RiftReference link2 = LocalReference.tryMakeRelative(riftB.get(), riftA.get());

			manager.consume(point_a, rift -> addLink(rift, link1));

			if(connection == ConnectionType.BOTH) manager.consume(point_b, rift -> addLink(rift, link2));
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("point_a", point_a)
				.add("point_b", point_b)
				.add("connection", connection.getSerializedName())
				.toString();
	}

	private boolean addLink(RiftBlockEntity rift, RiftReference link) {
		rift.setDestination(link);
		return true;
	}

	public enum ConnectionType implements StringRepresentable {
		BOTH("both"),
		ONE_WAY("one_way");

		private String id;

		ConnectionType(String id) {
			this.id = id;
		}

		@Override
		public String getSerializedName() {
			return id;
		}

		public static ConnectionType fromString(String name) {
			return "one_way".equalsIgnoreCase(name) ? ONE_WAY : BOTH;
		}

	}
}
