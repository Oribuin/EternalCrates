package xyz.oribuin.eternalcrates.nms.v1_17_R1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.nms.NMSHandler;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class NMSHandlerImpl implements NMSHandler {

    private static final AtomicInteger ENTITY_ID;

    static {
        try {
            // field remapped is ENTITY_COUNTER, but you can't get the remapped declared field.
            Field entityCount = Class.forName("net.minecraft.world.entity.Entity").getDeclaredField("b");
            entityCount.setAccessible(true);
            ENTITY_ID = (AtomicInteger) entityCount.get(null);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public Entity createClientsideEntity(Player player, Location loc, EntityType entityType) {

        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Entity> nmsEntityType = Registry.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(entityType.getKey()));

        final BlockPos blockPos = new BlockPos(loc.getX(), loc.getY(), loc.getZ());
        ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(this.getNewEntityId(), UUID.randomUUID(), loc.getX(), loc.getY(), loc.getZ(), 0f, 0f, nmsEntityType, 0, Vec3.atCenterOf(blockPos));
        serverPlayer.connection.send(packet);

        return null;
    }

    //    @Override
    //    public Entity updateEntity(Player player, Entity entity) {
    //        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
    //        final CraftEntity craftEntity = (CraftEntity) entity;
    //        final Location loc = entity.getLocation();
    //
    //        ClientboundMoveEntityPacket.PosRot packet = new ClientboundMoveEntityPacket.PosRot(craftEntity.getEntityId(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), entity.isOnGround());
    //
    //    }

    private int getNewEntityId() {
        return ENTITY_ID.incrementAndGet();
    }


}
