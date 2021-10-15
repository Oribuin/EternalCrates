package xyz.oribuin.eternalcrates.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;

public class AnimationListeners implements Listener {

    private final NamespacedKey chickenKey;

    public AnimationListeners(final EternalCrates plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.chickenKey = new NamespacedKey(plugin, "chicken");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();
        if (event.getDamager().hasMetadata("eternalcrates:firework") || cont.has(chickenKey, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();

        if (!cont.has(chickenKey, PersistentDataType.INTEGER))
            return;

        event.setDroppedExp(0);
        event.getDrops().clear();
    }

}
