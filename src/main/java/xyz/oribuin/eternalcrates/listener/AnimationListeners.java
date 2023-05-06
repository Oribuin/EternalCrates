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

public class AnimationListeners implements Listener {

    private final NamespacedKey key = EternalCrates.getEntityKey();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();
        final PersistentDataContainer damager = event.getDamager().getPersistentDataContainer();

        if (cont.has(key, PersistentDataType.INTEGER) || damager.has(key, PersistentDataType.INTEGER))
            event.setCancelled(true);

    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();

        if (!cont.has(key, PersistentDataType.INTEGER))
            return;

        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            final PersistentDataContainer cont = entity.getPersistentDataContainer();
            if (cont.has(key, PersistentDataType.INTEGER)) {
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();

        if (!cont.has(key, PersistentDataType.INTEGER))
            return;

        if (event.getEntity().getType() == EntityType.FALLING_BLOCK) {
            event.setCancelled(true);
        }
    }
}
