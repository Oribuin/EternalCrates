package xyz.oribuin.eternalcrates.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;
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
            Field entityCount = Class.forName("net.minecraft.server.v1_16_R3.Entity").getDeclaredField("entityCount");
            entityCount.setAccessible(true);
            ENTITY_ID = (AtomicInteger) entityCount.get(null);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public Entity createClientsideEntity(Player player, Location loc, EntityType entityType) {

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        EntityTypes<? extends net.minecraft.server.v1_16_R3.Entity> nmsEntityType = IRegistry.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(entityType.getKey()));
        final BlockPosition blockPos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());

        final PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(this.getNewEntityId(), UUID.randomUUID(), loc.getX(), loc.getY(), loc.getZ(), 0f, 0f, nmsEntityType, 0, Vec3D.a(blockPos));
        entityPlayer.playerConnection.sendPacket(packet);

        return null;
    }

    private int getNewEntityId() {
        return ENTITY_ID.incrementAndGet();
    }

}
