package org.dimdev.dimdoors.pockets.virtual.selection;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.dimdev.dimdoors.api.util.WeightedList;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.reference.PocketGeneratorReference;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public abstract class AbstractVirtualPocketList extends WeightedList<VirtualPocket, PocketGenerationContext> implements ImplementedVirtualPocket {
	public static <T extends AbstractVirtualPocketList> Products.P1<RecordCodecBuilder.Mu<T>, String> commonFields(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(Codec.STRING.optionalFieldOf("resourceKey", null).forGetter(AbstractVirtualPocketList::getResourceKey));
	}

	private String resourceKey = null;

	public AbstractVirtualPocketList(String resourceKey) {
		this.resourceKey = resourceKey;
	}

	@Override
	public void setResourceKey(String resourceKey) {
		this.resourceKey = resourceKey;
	}

	@Override
	public String getResourceKey() {
		return resourceKey;
	}

	@Override
	public Tag toNbt(CompoundTag nbt) {
		if (this.getResourceKey() != null) {
			return StringTag.valueOf(this.getResourceKey());
		}
		return toNbtInternal(nbt);
	}

	// utility so the first part of toNbt can be extracted into default method
	// at this point we know for a fact, that we need to serialize into the CompoundTag
	// overwrite in subclass
	protected CompoundTag toNbtInternal(CompoundTag nbt) {
		return this.getType().toNbt(nbt);
	}

	@Override
	public Pocket prepareAndPlacePocket(PocketGenerationContext context) {
		return getNextPocketGeneratorReference(context).prepareAndPlacePocket(context);
	}

	public PocketGeneratorReference getNextPocketGeneratorReference(PocketGenerationContext context) {
		return getNextRandomWeighted(context).getNextPocketGeneratorReference(context);
	}

	public PocketGeneratorReference peekNextPocketGeneratorReference(PocketGenerationContext context) {
		return peekNextRandomWeighted(context).peekNextPocketGeneratorReference(context);
	}

	@Override
	public double getWeight(PocketGenerationContext context) {
		return getTotalWeight(context);
	}

	@Override
	public void init() {
		this.forEach(VirtualPocket::init);
	}
}
