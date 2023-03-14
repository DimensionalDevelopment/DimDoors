package org.dimdev.dimdoors.block.entity;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.api.block.entity.MutableBlockEntityType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.data.DoorData;

public class ModBlockEntityTypes {
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Constants.MODID);

	public static final RegistryObject<BlockEntityType<DetachedRiftBlockEntity>> DETACHED_RIFT = BLOCK_ENTITIES.register("detached_rift", () -> register(DetachedRiftBlockEntity::new, ModBlocks.DETACHED_RIFT.get()));

	public static final RegistryObject<MutableBlockEntityType<EntranceRiftBlockEntity>> ENTRANCE_RIFT = BLOCK_ENTITIES.register("entrance_rift", () -> registerMutable(EntranceRiftBlockEntity::new, ArrayUtils.add(DoorData.DOORS.toArray(new Block[0]), ModBlocks.DIMENSIONAL_PORTAL.get())));

    public static final RegistryObject<BlockEntityType<TesselatingLoomBlockEntity>> TESSELATING_LOOM = BLOCK_ENTITIES.register("tesselating_loom", () -> register(TesselatingLoomBlockEntity::new, ModBlocks.TESSELATING_LOOM.get()));


    private static <E extends BlockEntity> BlockEntityType<E> register(BlockEntityType.BlockEntitySupplier<E> factory, Block... blocks) {
		return BlockEntityType.Builder.of(factory, blocks).build(null);
	}

	private static <E extends BlockEntity> MutableBlockEntityType<E> registerMutable(MutableBlockEntityType.BlockEntityFactory<E> factory, Block... blocks) {
		return MutableBlockEntityType.Builder.create(factory, blocks).build();
	}

	public static void init(IEventBus eventBus) {
		BLOCK_ENTITIES.register(eventBus);
	}
}
