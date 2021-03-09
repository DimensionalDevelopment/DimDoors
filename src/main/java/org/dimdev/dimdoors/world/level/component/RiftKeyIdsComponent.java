package org.dimdev.dimdoors.world.level.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import org.dimdev.dimdoors.mixin.ListTagAccessor;
import org.dimdev.dimdoors.world.level.DimensionalDoorsComponents;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

import net.fabricmc.fabric.api.util.NbtType;

public class RiftKeyIdsComponent extends ItemComponent {
	public RiftKeyIdsComponent(ItemStack stack) {
		super(stack);
		if (!hasTag("ids")) {
			this.putList("ids", ListTagAccessor.createListTag(new ArrayList<>(), (byte) NbtType.INT_ARRAY));
		}
	}

	public void addId(UUID uuid) {
		if (!hasId(uuid)) this.getList("ids", NbtType.INT_ARRAY).add(new IntArrayTag(DynamicSerializableUuid.toIntArray(uuid)));
	}

	public void addAll(Iterable<UUID> uuids) {
		uuids.forEach(this::addId);
	}

	public boolean hasId(UUID uuid) {
		return this.getList("ids", NbtType.INT_ARRAY).contains(new IntArrayTag(DynamicSerializableUuid.toIntArray(uuid)));
	}

	public boolean remove(UUID uuid) {
		return this.getList("ids", NbtType.INT_ARRAY).remove(new IntArrayTag(DynamicSerializableUuid.toIntArray(uuid)));
	}

	public boolean isEmpty() {
		return this.getList("ids", NbtType.INT_ARRAY).isEmpty();
	}

	public List<UUID> getIds() {
		return this.getList("ids", NbtType.INT_ARRAY)
				.stream()
				.map(IntArrayTag.class::cast)
				.map(IntArrayTag::getIntArray)
				.map(DynamicSerializableUuid::toUuid)
				.collect(Collectors.toList());
	}

	public static RiftKeyIdsComponent get(ItemStack stack) {
		return DimensionalDoorsComponents.RIFT_KEY_IDS_COMPONENT_KEY.get(stack);
	}
}
