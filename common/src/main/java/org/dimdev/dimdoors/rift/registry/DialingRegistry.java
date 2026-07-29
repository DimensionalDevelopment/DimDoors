//package org.dimdev.dimdoors.rift.registry;
//
//import net.minecraft.core.HolderLookup;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.datafix.DataFixTypes;
//import net.minecraft.world.level.saveddata.SavedData;
//import org.dimdev.dimdoors.world.pocket.type.PocketImpl;
//
//public class DialingRegistry extends SavedData {
//    public static final int VERSION = 0;
//
//    private static final String DATA_NAME = "dialing_registry";
//
//    private static DialingRegistry INSTANCE;
//
//    public DialingRegistry() {
//
//    }
//
//    public DialingRegistry(CompoundTag nbt, HolderLookup.Provider provider) {
//
//    }
//
//    public static void init(MinecraftServer server) {
//        INSTANCE = server.overworld().getDataStorage().computeIfAbsent(new Factory<DialingRegistry>(DialingRegistry::new, DialingRegistry::new, DataFixTypes.LEVEL), DATA_NAME);
//    }
//
//    @Override
//    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
//        return ;
//    }
//
//    public static DialingRegistry getInstance() {
//        if (INSTANCE == null) {
//            throw new IllegalStateException("DialingRegistry has not been initialized.");
//        }
//
//        return INSTANCE;
//    }
//
//    public PocketImpl getDialingPocket(int id) {
//        return null;
//    }
//}
