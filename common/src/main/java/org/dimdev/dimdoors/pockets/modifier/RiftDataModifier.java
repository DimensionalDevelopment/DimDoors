package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.api.util.NbtEquations;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiftDataModifier extends AbstractModifier {
	public static final String KEY = "rift_data";

	private CompoundTag doorData;
	private String doorDataReference;
	private List<Integer> ids;

	@Override
	public Modifier fromNbt(CompoundTag nbt, ResourceManager allowReference) {
		// TODO: RiftData via ResourceManager
		if (nbt.getTagType("rift_data") == Tag.TAG_STRING) {
			doorDataReference = nbt.getString("rift_data");
			doorData = PocketLoader.getInstance().getDataNbtCompound(doorDataReference);
		}
		else if (nbt.getTagType("rift_data") == Tag.TAG_COMPOUND) doorData = nbt.getCompound("rift_data");

		ids = stream(nbt.getByteArray("ids")).boxed().collect(Collectors.toList());
		return this;
	}

	public static IntStream stream(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		return IntStream.generate(buffer::get).limit(buffer.remaining());
	}

	public static byte[] toByteArray(int[] ints) {
		byte[] bytes = new byte[ints.length];
		for (int i = 0; i < ints.length; i++) {
			bytes[i] = (byte) ints[i];
		}
		return bytes;
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		if (doorDataReference != null) nbt.putString("rift_data", doorDataReference);
		else if (doorData != null) nbt.put("rift_data", doorData);
		nbt.putByteArray("ids", toByteArray(ids.stream().mapToInt(Integer::intValue).toArray()));
		return nbt;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.RIFT_DATA_MODIFIER_TYPE.get();
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

	private Consumer<RiftBlockEntity> solveData(CompoundTag doorData, Map<String, Double> variableMap) {
		CompoundTag solvedDoorData = NbtEquations.solveNbtCompoundEquations(doorData, variableMap);

		return rift -> rift.setData(RiftData.fromNbt(solvedDoorData));
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {
	}
}
