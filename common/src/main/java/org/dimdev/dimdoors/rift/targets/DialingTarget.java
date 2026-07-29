//package org.dimdev.dimdoors.rift.targets;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.core.Rotations;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.phys.Vec3;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.dimdev.dimdoors.api.rift.target.EntityTarget;
//import org.dimdev.dimdoors.api.util.Location;
//import org.dimdev.dimdoors.pockets.PocketGenerator;
//import org.dimdev.dimdoors.rift.registry.DialingRegistry;
//import org.dimdev.dimdoors.rift.registry.PocketRegistry;
//import org.dimdev.dimdoors.world.pocket.PrivateRegistry;
//import org.dimdev.dimdoors.world.pocket.VirtualLocation;
//import org.dimdev.dimdoors.world.pocket.type.Pocket;
//import org.dimdev.dimdoors.world.pocket.type.PocketImpl;
//import org.dimdev.limlib.api.util.EntityUtils;
//
//import java.util.UUID;
//
//public class DialingTarget extends VirtualTarget<DialingTarget> implements EntityTarget {
//    private static final Logger LOGGER = LogManager.getLogger();
//
//    public static final Codec<DialingTarget> CDOEC = RecordCodecBuilder.<DialingTarget>mapCodec(instance -> {
//       return instance.group(Codec.INT.fieldOf("id").forGetter(DialingTarget::getId)).apply(instance, DialingTarget::new);
//    });
//
//    private int id;
//
//    public DialingTarget(int id) {
//        this.id = id;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    @Override
//    public VirtualTarget.VirtualTargetType<DialingTarget> getType() {
//        return VirtualTarget.VirtualTargetType.DIALING;
//    }
//
//    @Override
//    public DialingTarget copy() {
//        return new DialingTarget(id);
//    }
//
//    @Override
//    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
//        UUID uuid = EntityUtils.getOwnerPlayerUuid(entity);
//
//        VirtualLocation virtualLocation = VirtualLocation.fromLocation(this.location);
//
//        PocketImpl pocket = DialingRegistry.getInstance().getDialingPocket(id);
//
//        if(pocket == null) {
//            pocket = this.generateDialingPocket(uuid, id, virtualLocation);
//        }
//
//        return false;
//    }
//
//    private Pocket<?, ?> generateDialingPocket(UUID uuid, VirtualLocation virtualLocation) {
//        Pocket<?, ?> pocket = PocketGenerator.generateDialingPocket(new VirtualLocation(virtualLocation.getWorld(), virtualLocation.getX(), virtualLocation.getZ(), -1));
//
//
//        return pocket;
//    }
//}
