package org.dimdev.dimdoors.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.item.ModDataComponentTypes;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.loot.ModLootTables;
import org.dimdev.dimdoors.rift.item.ItemRiftRegistry;
import org.dimdev.dimdoors.util.GrindstoneAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public class GrindstoneOutputSlotMixin extends Slot {
    @Shadow(aliases = "field_16780") @Final GrindstoneMenu this$0;

    public GrindstoneOutputSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void dimdoors$onTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (!stack.is(ModItems.PORTABLE_RIFT)) return;

        var grindstone = (GrindstoneAccessor) this$0;

        grindstone.getAccess().execute((level, pos) -> {
            if (!(level instanceof ServerLevel serverLevel)) return;

            ItemRiftRegistry.Companion.getInstance().detach(grindstone.resultSlots().getItem(0));

            LootParams ctx = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(pos))
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .create(LootContextParamSets.BLOCK_USE);

            serverLevel.getServer().reloadableRegistries().getLootTable(ModLootTables.REMOVED_RIFT).getRandomItems(ctx)
                    .forEach(drop -> Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop));
        });
    }
}