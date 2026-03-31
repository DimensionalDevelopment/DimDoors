package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class RiftDataModifier implements Modifier {
    public static final MapCodec<RiftDataModifier> CODEC = RecordCodecBuilder.<RiftDataModifier>mapCodec(instance -> instance.group(
            RiftData.CODEC.optionalFieldOf("rift_data").forGetter(a -> Optional.ofNullable(a.doorData)),
                    Codec.INT_STREAM.xmap(a -> a.boxed().toList(), integers -> integers.stream().mapToInt(Integer::intValue)).fieldOf("id").forGetter(a -> a.ids))
            .apply(instance, RiftDataModifier::new));


	public static final String KEY = "rift_data";

	private RiftData doorData;
	private List<Integer> ids;

    public RiftDataModifier(Optional<RiftData> doorData, List<Integer> ids) {
        this.doorData = doorData.orElse(null);
        this.ids = ids;
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
	public CompoundTag toNbtInternal(CompoundTag nbt, HolderLookup.Provider provider, boolean allowReference) {
		super.toNbtInternal(nbt, provider, allowReference);

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
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("doorData", doorData)
				.add("doorDataReference", doorDataReference)
				.add("ids", ids)
				.toString();
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
//		Map<String, Double> variableMap = manager.getPocket().toVariableMap(new HashMap<>()); //TODO: Molang Expression Support

		Consumer<RiftBlockEntity> riftBlockEntityConsumer;

		if (doorData == null) {
			riftBlockEntityConsumer = rift -> rift.setDestination(VirtualTarget.NoneTarget.INSTANCE);
		} else {
			riftBlockEntityConsumer = rift -> rift.setData(doorData);
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