package org.dimdev.dimdoors.pockets.modifier;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import com.google.common.base.MoreObjects;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.api.util.NbtEquations;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class RiftDataModifier implements Modifier {
	public static final String KEY = "rift_data";

	private NbtCompound doorData;
	private String doorDataReference;
	private List<Integer> ids;

	@Override
	public Modifier fromNbt(NbtCompound nbt) {
		if (nbt.getType("rift_data") == NbtType.STRING) {
			doorDataReference = nbt.getString("rift_data");
			doorData = PocketLoader.getInstance().getDataNbtCompound(doorDataReference);
		}
		else if (nbt.getType("rift_data") == NbtType.COMPOUND) doorData = nbt.getCompound("rift_data");

		ids = stream(nbt.getByteArray("ids")).boxed().collect(Collectors.toList());
		return this;
	}

	public static IntStream stream(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		return IntStream.generate(buffer::get).limit(buffer.remaining());
	}

	private static byte[] toByteArray(int[] ints) {
		byte[] bytes = new byte[ints.length];
		for (int i = 0; i < ints.length; i++) {
			bytes[i] = (byte) ints[i];
		}
		return bytes;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		Modifier.super.toNbt(nbt);

		if (doorDataReference != null) nbt.putString("rift_data", doorDataReference);
		else if (doorData != null) nbt.put("rift_data", doorData);
		nbt.putByteArray("ids", toByteArray(ids.stream().mapToInt(Integer::intValue).toArray()));
		return nbt;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.RIFT_DATA_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
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

		Consumer<RiftBlockEntity> riftBlockEntityConsumer;

		if (doorData == null) {
			riftBlockEntityConsumer = rift -> rift.setDestination(VirtualTarget.NoneTarget.INSTANCE);
		} else {
			NbtCompound solvedDoorData = NbtEquations.solveNbtCompoundEquations(doorData, variableMap);

			riftBlockEntityConsumer = rift -> rift.setData(RiftData.fromNbt(solvedDoorData));
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

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
	}
}
