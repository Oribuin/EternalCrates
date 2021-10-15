package xyz.oribuin.eternalcrates.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;

public record AnimationListeners(EternalCrates plugin) implements Listener {

    public AnimationListeners(final EternalCrates plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!event.getDamager().hasMetadata("eternalcrates:firework"))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        final PersistentDataContainer cont = event.getEntity().getPersistentDataContainer();

        if (!cont.has(new NamespacedKey(EternalCrates.getInstance(), "chicken"), PersistentDataType.INTEGER))
            return;

        event.setDroppedExp(0);
        event.getDrops().clear();
    }
}
