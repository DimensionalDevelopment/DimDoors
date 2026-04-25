//package org.dimdev.dimdoors.world.pocket.type.addon;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.nbt.NbtOps;
//import net.minecraft.nbt.Tag;
//import net.minecraft.resources.ResourceLocation;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//public abstract class AddonContainer<T extends ContainedAddon> implements PocketAddon {
//    protected ResourceLocation id;
//    protected List<T> addons = new ArrayList<>();
//
//    public AddonContainer() {
//    }
//
//    public void setId(ResourceLocation id) {
//    this.id = id;
//    }
//
//    public void addAll(Collection<T> addons) {
//    this.addons.addAll(addons);
//    }
//
//    public void add(T addon) {
//    this.addons.add(addon);
//    }
//
//    @Override
//    public CompoundTag toNbt(CompoundTag nbt) {
//    PocketAddon.super.toNbt(nbt);
//
//        PocketAddon.LIST_CODEC.encodeStart(NbtOps.INSTANCE, addons).result().ifPresent(tag -> nbt.put("addons", tag));
//
//    ListTag addonsTag = new ListTag();
//    for(T addon : addons) {
//        addonsTag.add(addon.toNbt(new CompoundTag()));
//    }
//    nbt.put("addons", addonsTag);
//
//    return nbt;
//    }
//
//    @Override
//    public PocketAddon fromNbt(CompoundTag nbt) {
//    addons.clear();
//    nbt.getList("addons", ListTag.TAG_COMPOUND).stream().map(CompoundTag.class::cast).map(PocketAddon::deserialize).filter(ContainedAddon.class::isInstance).map(ContainedAddon.class::cast).forEach((Consumer<PocketAddon>) pocketAddon -> addons.add((T) pocketAddon));
//    return this;
//    }
//
//    @Override
//    public ResourceLocation getId() {
//    return id;
//    }
//}
//
