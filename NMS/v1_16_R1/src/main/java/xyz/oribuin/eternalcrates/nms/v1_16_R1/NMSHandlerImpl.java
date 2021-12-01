package xyz.oribuin.eternalcrates.nms.v1_16_R1;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.nms.NMSHandler;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class NMSHandlerImpl implements NMSHandler {

    private static final AtomicInteger ENTITY_ID;

    static {
        try {
            Field entityCount = Class.forName("net.minecraft.server.v1_16_R2.Entity").getDeclaredField("entityCount");
            entityCount.setAccessible(true);
            ENTITY_ID = (AtomicInteger) entityCount.get(null);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public Entity createClientsideEntity(Player player, Location loc, EntityType entityType) {

        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        EntityTypes<? extends net.minecraft.server.v1_16_R1.Entity> nmsEntityType = IRegistry.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(entityType.getKey()));
        final BlockPosition blockPos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());

        final PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(this.getNewEntityId(), UUID.randomUUID(), loc.getX(), loc.getY(), loc.getZ(), 0f, 0f, nmsEntityType, 0, Vec3D.a(blockPos));
        entityPlayer.playerConnection.sendPacket(packet);

        return null;
    }

    @Override
    public ItemStack setString(ItemStack item, String key, String value) {
        final net.minecraft.server.v1_16_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = itemStack.getOrCreateTag();
        tag.setString(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setInt(ItemStack item, String key, int value) {
        final net.minecraft.server.v1_16_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = itemStack.getOrCreateTag();
        tag.setInt(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setLong(ItemStack item, String key, long value) {
        final net.minecraft.server.v1_16_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = itemStack.getOrCreateTag();
        tag.setLong(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setDouble(ItemStack item, String key, double value) {
        final net.minecraft.server.v1_16_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = itemStack.getOrCreateTag();
        tag.setDouble(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setBoolean(ItemStack item, String key, boolean value) {
        final net.minecraft.server.v1_16_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = itemStack.getOrCreateTag();
        tag.setBoolean(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    private int getNewEntityId() {
        return ENTITY_ID.incrementAndGet();
    }

}
