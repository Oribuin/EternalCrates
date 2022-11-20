package xyz.oribuin.eternalcrates.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;

public class AnimationListeners implements Listener {

    private final NamespacedKey key = EternalCrates.getEntityKey();

    public AnimationListeners(final EternalCrates plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        final var cont = event.getEntity().getPersistentDataContainer();
        final var damager = event.getDamager().getPersistentDataContainer();
        if (cont.has(key, PersistentDataType.INTEGER) || damager.has(key, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        final var cont = event.getEntity().getPersistentDataContainer();

        if (!cont.has(key, PersistentDataType.INTEGER))
            return;

        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (var entity : event.getChunk().getEntities()) {
            final var cont = entity.getPersistentDataContainer();
            if (cont.has(key, PersistentDataType.INTEGER)) {
                entity.remove();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChange(EntityChangeBlockEvent event) {
        final var cont = event.getEntity().getPersistentDataContainer();

        if (!cont.has(key, PersistentDataType.INTEGER))
            return;

        if (event.getEntity().getType() == EntityType.FALLING_BLOCK) {
            event.setCancelled(true);
        }
    }
}
