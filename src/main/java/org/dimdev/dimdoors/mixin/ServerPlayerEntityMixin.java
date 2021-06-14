package org.dimdev.dimdoors.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerRecipeBook;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.block.UnravelledFabricBlock;
import org.dimdev.dimdoors.criteria.ModCriteria;
import org.dimdev.dimdoors.enchantment.ModEnchants;
import org.dimdev.dimdoors.entity.limbo.LimboEntranceSource;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.network.ExtendedServerPlayNetworkHandler;
import org.dimdev.dimdoors.network.packet.s2c.PlayerInventorySlotUpdateS2CPacket;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.component.PlayerModifiersComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Random;

@Mixin(value = ServerPlayerEntity.class, priority = 900)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
	@Shadow
	@Final
	private ServerRecipeBook recipeBook;

	@Shadow
	public abstract void readCustomDataFromNbt(NbtCompound nbt);

	private static final float RANDOM_ACTION_CHANCE = 0.1F;
	private static final float CHANCE_TO_DECREASE_ARMOR_DURABILITY = 0.03F;
	private static final float CHANCE_TO_REPLACE_ITEMSLOT_WITH_UNRAVLED_FABRIC = 0.005F;
	private static final float CHANCE_TO_ENCHANT_WITH_FRAY = 0.01F;
	private static final float CHANCE_TO_MAKE_LIMBO_LIKE_OTHER_DIMENSIONS = 0.1F;
	private static final float RANDOM_INCREMENT_FRAY_CHANCE = 0.1F;
	private static final int CHUNK_SIZES = 25;
	private static final int POSITION_AWAY = 50;
	private static final float RANDOM_LIQUID_CHANCE = 0.7F;
	Random random = new Random();

	public ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void playerTickMixin(CallbackInfo ci) {
		if (random.nextFloat() <= RANDOM_ACTION_CHANCE) {
			if(random.nextFloat() <= RANDOM_INCREMENT_FRAY_CHANCE && PlayerModifiersComponent.getFray((PlayerEntity) (Object)this) < 180 && ModDimensions.isLimboDimension((((PlayerEntity)(Object)(this)).getEntityWorld()))) {
				PlayerModifiersComponent.incrementFray((PlayerEntity) (Object)this, 1);
			}
			if (PlayerModifiersComponent.getFray(this) >= 125) {
				doRandomFunction(this);
			}
			if(ModDimensions.isLimboDimension(((PlayerEntity)(Object)(this)).getEntityWorld())) {
				tryAddingFrayEnchantment((PlayerEntity) (Object)this);
				tryMakingLimboLikeOtherDimensions((PlayerEntity)(Object)this);
			}
		}

	}
	private boolean isValidBlockToReplace(World world, BlockPos pos) {
		return world.getBlockState(pos.up()).isAir() && world.getBlockState(pos).getBlock() instanceof UnravelledFabricBlock;
	}
	private void makeLimboLikeOverworld(PlayerEntity player) {
		/*
		World world = player.getEntityWorld();
		BlockPos pos = player.getBlockPos().add(random.nextInt(random.nextInt(POSITION_AWAY)), 0, random.nextInt(POSITION_AWAY));
		if(random.nextFloat() <= RANDOM_LIQUID_CHANCE) {
			makeSpotOfLiquid(world, pos, Blocks.WATER.getDefaultState(), 3);
		}
		BlockPos.iterateOutwards(pos, CHUNK_SIZES, 15, CHUNK_SIZES).forEach( (blockPos -> {
			if(isValidBlockToReplace(world, blockPos)) {
				world.setBlockState(blockPos, Blocks.GRASS_BLOCK.getDefaultState());
			}

		}));

		 */

	}
	private void makeLimboLikeEnd(PlayerEntity player) {
		/*
		World world = player.getEntityWorld();
		BlockPos pos = player.getBlockPos().add(random.nextInt(POSITION_AWAY), 0, random.nextInt(POSITION_AWAY));
		BlockPos.iterateOutwards(pos, CHUNK_SIZES, 15, CHUNK_SIZES).forEach( (blockPos -> {
			if(isValidBlockToReplace(world, blockPos)) {
				world.setBlockState(blockPos, Blocks.END_STONE.getDefaultState());
			}

		}));

		 */
	}

	private void makeSpotOfLiquid(World world, BlockPos pos, BlockState state, int range) {

		BlockPos.iterateOutwards(pos, random.nextInt(range), random.nextInt(range), random.nextInt(range)).forEach( (blockPos -> {
			if(isValidBlockToReplace(world, blockPos)) {
				world.setBlockState(blockPos, state);
			}

		}));

	}

	private void makeLimboLikeNether(PlayerEntity player) {
		/*
		World world = player.getEntityWorld();
		BlockPos pos = player.getBlockPos().add(random.nextInt(POSITION_AWAY), 0, random.nextInt(POSITION_AWAY));
		if(random.nextFloat() <= RANDOM_LIQUID_CHANCE) {
			makeSpotOfLiquid(world, pos, Blocks.LAVA.getDefaultState(), 10);
		}
		BlockPos.iterateOutwards(pos, CHUNK_SIZES, 15, CHUNK_SIZES).forEach( (blockPos -> {
			if(isValidBlockToReplace(world, blockPos)) {
				world.setBlockState(blockPos, Blocks.NETHERRACK.getDefaultState());
			}

		}));

		 */
	}
	private void tryMakingLimboLikeOtherDimensions(PlayerEntity player) {
		if(random.nextFloat() > CHANCE_TO_MAKE_LIMBO_LIKE_OTHER_DIMENSIONS) {
			return;
		}
		switch (random.nextInt(3)) {
			case 0 : makeLimboLikeOverworld(player); break;
			case 1 : makeLimboLikeNether(player); break;
			case 2 : makeLimboLikeEnd(player); break;
		}
	}

	private void doRandomFunction(LivingEntity player) {
		switch (random.nextInt(2)) {
			case 0:
				decreaseArmorDurability((PlayerEntity) player);
				break;
			case 1:
				addRandomUnravledFabric((PlayerEntity) player);
				break;
			default:
		}

	}

	private void tryAddingFrayEnchantment(PlayerEntity player) {
		if (!(random.nextFloat() <= CHANCE_TO_ENCHANT_WITH_FRAY)) {
			return;
		}
		int slot = random.nextInt(player.getInventory().size());
		if (!player.getInventory().getStack(slot).isEnchantable()) {
			return;
		}
		ItemStack stack = player.getInventory().getStack(slot);
		stack.addEnchantment(ModEnchants.FRAYED_ENCHANTMENT, 1);
		player.getInventory().setStack(slot, stack);
		((ExtendedServerPlayNetworkHandler) (Object) ((ServerPlayerEntity) (Object) this).networkHandler).getDimDoorsPacketHandler().sendPacket(new PlayerInventorySlotUpdateS2CPacket(slot, stack));

	}

	//TODO: Fix this shit so it syncs.
	private void addRandomUnravledFabric(PlayerEntity player) {
		if (PlayerModifiersComponent.getFray(player) < DimensionalDoorsInitializer.getConfig().getPlayerConfig().fray.unravledFabricInInventoryFray)
			return;
		if (!(random.nextFloat() <= CHANCE_TO_REPLACE_ITEMSLOT_WITH_UNRAVLED_FABRIC))
			return;

		int slot = random.nextInt(player.getInventory().main.size());

		if (!player.getInventory().main.get(slot).isEmpty() && !(player.getInventory().main.get(slot).getItem() == ModItems.UNRAVELLED_FABRIC))
			return;
		if (player.getInventory().main.get(slot).getCount() >= 64)
			return;
		ItemStack stack = new ItemStack(ModItems.UNRAVELLED_FABRIC, 1 + player.getInventory().main.get(slot).getCount());
		player.getInventory().main.set(slot, stack);
		((ExtendedServerPlayNetworkHandler) (Object) ((ServerPlayerEntity) (Object) this).networkHandler).getDimDoorsPacketHandler().sendPacket(new PlayerInventorySlotUpdateS2CPacket(slot, stack));
	}

	private void decreaseArmorDurability(PlayerEntity player) {
		if (PlayerModifiersComponent.getFray(player) < DimensionalDoorsInitializer.getConfig().getPlayerConfig().fray.unravledFabricInInventoryFray)
			return;
		for (int i = 0; i < player.getInventory().armor.size(); i++)
			if (random.nextFloat() <= CHANCE_TO_DECREASE_ARMOR_DURABILITY)
				player.getArmorItems().forEach((itemStack) -> {
					itemStack.setDamage(itemStack.getDamage() + 1);
				});
	}

	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	public void checkDeathServer(DamageSource source, CallbackInfo ci) {
		this.doOnDeathStuff(source, ci);
		if (ci.isCancelled()) {
			if (ModDimensions.isPocketDimension(this.world)) {
				this.incrementStat(ModStats.DEATHS_IN_POCKETS);
			}
			this.incrementStat(ModStats.TIMES_SENT_TO_LIMBO);
			TeleportUtil.teleportRandom(this, ModDimensions.LIMBO_DIMENSION, 384);
			//noinspection ConstantConditions
			LimboEntranceSource.ofDamageSource(source).broadcast((PlayerEntity) (Object) this, this.getServer());
		}
	}

	@Inject(method = "setSpawnPoint", at = @At("TAIL"))
	public void onSpawnPointSet(RegistryKey<World> dimension, BlockPos pos, float angle, boolean spawnPointSet, boolean bl, CallbackInfo ci) {
		if (ModDimensions.isPocketDimension(dimension)) {
			ModCriteria.POCKET_SPAWN_POINT_SET.trigger((ServerPlayerEntity) (Object) this);
		}
	}


}
