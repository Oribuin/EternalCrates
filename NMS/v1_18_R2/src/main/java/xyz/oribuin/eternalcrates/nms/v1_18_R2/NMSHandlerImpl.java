package xyz.oribuin.eternalcrates.nms.v1_18_R2;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
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

    @Override
    public ItemStack setString(ItemStack item, String key, String value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putString(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setInt(ItemStack item, String key, int value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putInt(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setLong(ItemStack item, String key, long value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putLong(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setDouble(ItemStack item, String key, double value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putDouble(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setBoolean(ItemStack item, String key, boolean value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putBoolean(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    private int getNewEntityId() {
        return ENTITY_ID.incrementAndGet();
    }

}
