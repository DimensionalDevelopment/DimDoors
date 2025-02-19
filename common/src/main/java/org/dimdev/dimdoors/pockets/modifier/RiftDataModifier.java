package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class RiftDataModifier implements Modifier {
	public static final MapCodec<RiftDataModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			RiftData.CODEC.fieldOf("rift_data").forGetter(a -> a.doorData),
			Codec.INT_STREAM.fieldOf("ids").xmap(intStream -> intStream.boxed().toList(), integers -> integers.stream().mapToInt(a -> a)).fieldOf("rift_data").forGetter(a -> a.ids)
	).apply(instance, RiftDataModifier::new));

	public static final String KEY = "rift_data";

	private RiftData doorData;
	private String doorDataReference;
	private List<Integer> ids;

	public RiftDataModifier(RiftData doorData, List<Integer> ids) {
		this.doorData = doorData;
		this.ids = ids;
	}

    public static IntStream stream(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		return IntStream.generate(buffer::get).limit(buffer.remaining());
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.RIFT_DATA_MODIFIER_TYPE.get();
	}

    @Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("doorData", doorData)
				.add("doorDataReference", doorDataReference)
				.add("ids", ids)
				.toString();
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		Map<String, Double> variableMap = manager.getPocket().toVariableMap(new HashMap<>());

		Consumer<RiftBlockEntity<?>> riftBlockEntityConsumer;

		if (doorData == null) {
			riftBlockEntityConsumer = rift -> rift.setDestination(VirtualTarget.NoneTarget.INSTANCE);
		} else {
			riftBlockEntityConsumer = solveData(doorData, variableMap);
		}

		manager.foreachConsume((id, rift) -> {
			if(ids.contains(id)) {
				riftBlockEntityConsumer.accept(rift);
				return true;
			} else {
				return false;
			}
		});
	}

	private Consumer<RiftBlockEntity<?>> solveData(RiftData doorData, Map<String, Double> variableMap) {
		CompoundTag solvedDoorData = (CompoundTag) NbtOps.INSTANCE.withEncoder(RiftData.CODEC).apply(doorData).getOrThrow();

		return rift -> rift.setData(RiftData.fromNbt(solvedDoorData));
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
	}
}
