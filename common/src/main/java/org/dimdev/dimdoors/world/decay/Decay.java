package org.dimdev.dimdoors.world.decay;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.network.ServerPacketHandler;
import org.dimdev.dimdoors.network.packet.s2c.RenderBreakBlockS2CPacket;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides methods for applying decay. Decay refers to the effect that most blocks placed next to certain blocks like unraveled fabric
 * change into simpler forms ultimately becoming in most cases unraveled Fabric as time passes.
 */
public final class Decay {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceKey<Level>, Set<DecayTask>> DECAY_QUEUE = new HashMap<>();
    private static final int BREAK_BLOCK_STAGE = 5;
    private static final double BREAK_BLOCK_RENDER_DISTANCE = 100.0D;

    /**
     * Checks the blocks orthogonally around a given location (presumably the location of an Unraveled Fabric block)
     * and applies Limbo decay to them. This gives the impression that decay spreads outward from Unraveled Fabric.
     */
    public static void applySpreadDecay(ServerLevel world, BlockPos pos, RandomSource random, DecaySource source) {
        //Check if we randomly apply decay spread or not. This can be used to moderate the frequency of
        //full spread decay checks, which can also shift its performance impact on the game.
        if (random.nextDouble() < DimensionalDoors.getConfig().getDecayConfig().decaySpreadChance) {
            BlockState origin = world.getBlockState(pos);

            //Apply decay to the blocks above, below, and on all four sides.
            // TODO: make max amount configurable
            int decayAmount = random.nextInt(5) + 1;
            List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
            for (int i = 0; i < decayAmount; i++) {
                decayBlock(world, pos, origin, directions.remove(random.nextInt(5 - i)), source);
            }
        }
    }

    /**
     * Checks if a block can be decayed and, if so, changes it to the next block ID along the decay sequence.
     */
    public static void decayBlock(ServerLevel world, BlockPos originPos, BlockState originBlockState, Direction direction, DecaySource source) {
        DecayContext context = DecayContext.create(world, originPos, originBlockState, direction, source);

        var patterns = DecayLoader.getPatterns(context);

    for(DecayPatternHolder pattern : patterns) {
            if (pattern.value().test(context)) {
                sendBreakBlockProgress(world, context.targetBlockPos, BREAK_BLOCK_STAGE);
                world.playSound(null, context.targetBlockPos, ModSoundEvents.TEARING, SoundSource.BLOCKS, 0.5f, 1f);
                queueDecay(context, pattern, DimensionalDoors.getConfig().getDecayConfig().decayDelay);
                break;
            }
        }
    }

    public static void queueDecay(Decay.DecayContext context, DecayPatternHolder pattern, int delay) {
        DecayTask task = new DecayTask(context, pattern, delay);
        if (delay <= 0) {
            task.process();
        } else {
            DECAY_QUEUE.computeIfAbsent(context.world().dimension(), k -> new HashSet<>()).add(task);
        }
    }

    public static void tick(ServerLevel world) {
        ResourceKey<Level> key = world.dimension();
        if (DECAY_QUEUE.containsKey(key)) {
            Set<DecayTask> tasks = DECAY_QUEUE.get(key);
            Set<DecayTask> tasksToRun = tasks.stream().filter(DecayTask::reduceDelayIsDone).collect(Collectors.toSet());
            tasks.removeAll(tasksToRun);
            tasksToRun.forEach(task -> task.process());
        }
    }

    private static void sendBreakBlockProgress(ServerLevel world, BlockPos pos, int stage) {
        var packet = new RenderBreakBlockS2CPacket(pos, stage);
        world.getPlayers(EntitySelector.withinDistance(pos.getX(), pos.getY(), pos.getZ(), BREAK_BLOCK_RENDER_DISTANCE))
                .forEach(player -> ServerPacketHandler.sendPacket(player, packet));
    }

    public static class DecayLoader {
    private static final Logger LOGGER = LogManager.getLogger();

        private static final Map<ResourceKey<?>, List<DecayPatternHolder>> patterns = new HashMap<>();

        private static List<DecayPatternHolder> undiffernitatedPatterns = new ArrayList<>();

    public static void reload(HolderLookup.Provider provider, ResourceManager manager) {
            undiffernitatedPatterns = ResourceUtil.loadResourcePathToCollection(manager, "decay_patterns", ".json", new ArrayList<>(), ResourceUtil.JSON_READER.andThenReader(DecayLoader::loadPattern)).join();
        }

    private static DecayPatternHolder loadPattern(JsonElement json, ResourceLocation id) {
        return new DecayPatternHolder(id, JsonOps.INSTANCE.withDecoder(DecayPattern.CODEC).apply(json).getOrThrow().getFirst());
    }

    public static Collection<DecayPatternHolder> getPatterns(Object object) {
            if(object != null) {
                return switch (object) {
                    case FluidState state -> getPatterns(state.holder());
                    case BlockState state -> getPatterns(state.getBlockHolder());
                    case ResourceKey<?> key -> patterns.getOrDefault(key, Collections.emptyList());
                    case Holder<?> holder -> holder.unwrapKey().map(patterns::get).orElse(Collections.emptyList());
                    case DecayContext context -> {
                        Collection<DecayPatternHolder> patterns = DecayLoader.getPatterns(context.targetBlockState);
                        if(patterns.isEmpty()) patterns = DecayLoader.getPatterns(context.targetFluidState);
                        if(patterns.isEmpty()) patterns = DecayLoader.getPatterns(context.targetEntity());
                        if(patterns.isEmpty()) patterns = Collections.emptyList();
                        yield patterns;
                    }
                    case Painting painting -> getPatterns(painting.getVariant());
                    default -> Collections.emptyList();
                };
            }

            return Collections.emptyList();
        }

//    public static Collection<DecayPatternHolder> getPatterns(ResourceKey<Fluid> fluid) {
//        return fluidPatterns.getOrDefault(fluid, Collections.emptyList());
//    }

        public static Map<ResourceKey<?>, List<DecayPatternHolder>> getPatterns() {
            return patterns;
        }

        public static void populate(MinecraftServer server) {
            patterns.clear();
            var registry = DimensionalDoors.getServer().registryAccess();

            for (DecayPatternHolder pattern : undiffernitatedPatterns) {
                pattern.value().constructApplicable(registry).forEach(resourceKey -> patterns.computeIfAbsent(resourceKey, (b) -> new ArrayList<>()).add(pattern));
            }
        }
    }

    private static class DecayTask {
        private final Decay.DecayContext context;

        private final DecayPatternHolder processor;
        private int delay;


        public DecayTask(Decay.DecayContext context, DecayPatternHolder pattern, int delay) {
            this.context = context;
            this.processor = pattern;
            this.delay = delay;
        }

        public boolean reduceDelayIsDone() {
            return --delay <= 0;
        }

        public void process() {
            sendBreakBlockProgress(context.world(), context.targetBlockPos(), -1);

            if (context.source().decayIntoWorldThread()) {
                if (DimensionalDoors.getConfig().getDecayConfig().decaysIntoAir) {
                    var contents = DecayInventoryHelper.takeContents(context.world(), context.targetBlockPos());
                    context.world().setBlockAndUpdate(context.targetBlockPos, Blocks.AIR.defaultBlockState());
                    DecayInventoryHelper.drop(context.world(), context.targetBlockPos(), contents);
                } else processor.value().applyPattern(context);
            } else {
                processor.value().applyPattern(context);
            }
        }
    }

    public record DecayContext(ServerLevel world, BlockPos originBlockPos, BlockState originBlockState, BlockPos targetBlockPos, BlockState targetBlockState, FluidState targetFluidState, @Nullable Entity targetEntity, DecaySource source) {

        public static DecayContext create(ServerLevel world, BlockPos originBlockPos, BlockState originBlockState, Direction direction, DecaySource source) {
            var targetPos = originBlockPos.relative(direction);

            var targetBlockState = world.getBlockState(targetPos);

            FluidState targetFluidState = world.getFluidState(targetPos);

            var entity = world.getEntitiesOfClass(Painting.class, AABB.encapsulatingFullBlocks(targetPos, targetPos)).stream().findFirst().orElse(null);

            return new DecayContext(world, originBlockPos, originBlockState, targetPos, targetBlockState, targetFluidState, entity, source);
        }
    }
}
