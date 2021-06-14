package org.dimdev.dimdoors.world.level.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import org.apache.logging.log4j.Level;
import org.dimdev.dimdoors.DimensionalDoorsComponents;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.enchantment.ModEnchants;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.dimdev.dimdoors.util.schematic.SchematicPlacer.LOGGER;

public class PlayerModifiersComponent implements ComponentV3, AutoSyncedComponent {
	private int fray = 0;

	public PlayerModifiersComponent(@SuppressWarnings("unused") PlayerEntity player) {
	}

	@Override
	public void readFromNbt(NbtCompound nbt) {
		fray = nbt.getInt("Fray");
	}

	@Override
	public void writeToNbt(NbtCompound nbt) {
		nbt.putInt("Fray", fray);
	}

	@Override
	public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
		buf.writeVarInt(fray);
	}

	@Override
	public void applySyncPacket(PacketByteBuf buf) {
		fray = buf.readVarInt();
	}

	public int getFray() {
		return fray;
	}

	public void resetFray() {fray = 0;}

	public int incrementFray(int amount) {
		return (fray = MathHelper.clamp(fray + amount, 0, DimensionalDoorsInitializer.getConfig().getPlayerConfig().fray.maxFray));
	}

	public static PlayerModifiersComponent get(PlayerEntity player) {
		return DimensionalDoorsComponents.PLAYER_MODIFIERS_COMPONENT_KEY.get(player);
	}

	public static void resetFray(PlayerEntity player) {
		get(player).resetFray();
	}



	public static int getFray(LivingEntity player) {
		return get((PlayerEntity) player).getFray();
	}

	public static void sync(PlayerEntity player) {
		DimensionalDoorsComponents.PLAYER_MODIFIERS_COMPONENT_KEY.sync(player);
	}

	public static int incrementFray(PlayerEntity player, int amount) {
		for(int i = 0; i < player.getInventory().armor.size(); i++) {
			if(EnchantmentHelper.getLevel(ModEnchants.STRING_THEORY_ENCHANTMENT, player.getInventory().armor.get(i)) > 0) {
				amount *= 0.85;
			}
		}
		int v = get(player).incrementFray(amount);
		PlayerModifiersComponent.sync(player);
		if(getFray(player) == DimensionalDoorsInitializer.getConfig().getPlayerConfig().fray.maxFray) {
			killPlayer(player);
		}
		if(false) {
			LOGGER.log(Level.INFO, "fray amount is : " + getFray(player));
		}
		return v;
	}

	private static void killPlayer(PlayerEntity player) {
		player.kill();
		//Fray should be reset by mixinging into the player class and changing the kill event or some other funciton that happens when a player dies.
		//This is just temporary
		//On second thought, this should get rid of fray, and fray should stay with the player otherwise, that way they have to actively try to get rid of it without just like, dying.
		resetFray(player);
		player.getEntityWorld().setBlockState(player.getBlockPos(), ModBlocks.STONE_PLAYER.getDefaultState(), 3);
	}
}
