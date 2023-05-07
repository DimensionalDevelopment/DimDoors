package org.dimdev.dimdoors.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.SpongeSchematicReader;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.text.Text;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.util.schematic.Schematic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WorldeditHelper {
	static int load(CommandSourceStack source, PocketTemplate template) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		boolean async = DimensionalDoors.getConfig().getPocketsConfig().asyncWorldEditPocketLoading;
		Consumer<Runnable> taskAcceptor = async ? r -> source.getServer().execute(r) : Runnable::run;
		Runnable task = () -> {
			CompoundTag nbt = Schematic.toNbt(template.getSchematic());
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
				source.sendFeedback(Text.translatable("commands.pocket.loadedSchem", template.getId()), true);
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
