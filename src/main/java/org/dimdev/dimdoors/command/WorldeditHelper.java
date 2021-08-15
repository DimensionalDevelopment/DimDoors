package org.dimdev.dimdoors.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.SpongeSchematicReader;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.util.schematic.Schematic;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class WorldeditHelper {
	static int load(ServerCommandSource source, PocketTemplate template) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayer();
		boolean async = DimensionalDoorsInitializer.getConfig().getPocketsConfig().asyncWorldEditPocketLoading;
		Consumer<Runnable> taskAcceptor = async ? r -> source.getServer().execute(r) : Runnable::run;
		Runnable task = () -> {
			NbtCompound nbt = Schematic.toNbt(template.getSchematic());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			try {
				NbtIo.writeCompressed(nbt, stream);
			} catch (IOException e) {
				throw new RuntimeException(e); // Can't happen, the stream is a ByteArrayOutputStream
			}
			Clipboard clipboard;
			try {
				clipboard = new SpongeSchematicReader(new NBTInputStream(new ByteArrayInputStream(stream.toByteArray()))).read();
			} catch (IOException e) {
				throw new RuntimeException(e); // Can't happen, the stream is a ByteArrayInputStream
			}
			taskAcceptor.accept(() -> {
				WorldEdit.getInstance().getSessionManager().get(FabricAdapter.adaptPlayer(player)).setClipboard(new ClipboardHolder(clipboard));
				source.sendFeedback(new TranslatableText("commands.pocket.loadedSchem", template.getId()), true);
			});
		};
		if (async) {
			CompletableFuture.runAsync(task);
		} else {
			task.run();
		}
		return Command.SINGLE_SUCCESS;
	}

}
