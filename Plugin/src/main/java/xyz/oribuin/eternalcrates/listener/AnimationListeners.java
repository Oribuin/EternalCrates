package xyz.oribuin.eternalcrates.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;

import java.util.Arrays;

public class AnimationListeners implements Listener {

    private final NamespacedKey key = EternalCrates.getEntityKey();

    public AnimationListeners(final EternalCrates plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();
        if (event.getDamager().hasMetadata("eternalcrates:firework") || cont.has(key, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();

        if (!cont.has(key, PersistentDataType.INTEGER))
            return;

        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Arrays.stream(event.getChunk().getEntities())
                .filter(entity -> entity.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
                .forEach(Entity::remove);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChange(EntityChangeBlockEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();

        if (!cont.has(key, PersistentDataType.INTEGER))
            return;

        if (event.getEntity().getType() == EntityType.FALLING_BLOCK) {
            event.setCancelled(true);
        }
    }
}
