package xyz.oribuin.eternalcrates;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.hook.CratePlaceholders;
import xyz.oribuin.eternalcrates.listener.AnimationListeners;
import xyz.oribuin.eternalcrates.listener.CrateListeners;
import xyz.oribuin.eternalcrates.listener.PlayerListeners;
import xyz.oribuin.eternalcrates.manager.AnimationManager;
import xyz.oribuin.eternalcrates.manager.CommandManager;
import xyz.oribuin.eternalcrates.manager.ConfigurationManager;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.LocaleManager;
import xyz.oribuin.eternalcrates.manager.MenuManager;

import java.util.Arrays;
import java.util.List;

public class EternalCrates extends RosePlugin {

    private static EternalCrates instance;
    private static NamespacedKey entityKey;

    public EternalCrates() {
        super(97992, 14703, ConfigurationManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
        instance = this;
    }

    @Override
    public void enable() {
        entityKey = new NamespacedKey(this, "entity");

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new AnimationListeners(), this);
        this.getServer().getPluginManager().registerEvents(new CrateListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

        // Register PlaceholderAPI
        if (CratePlaceholders.isEnabled()) {
            new CratePlaceholders(this).register();
        }
    }

    @Override
    public void disable() {
        // Let's make sure there's no EternalCrates Entities left
        for (World world : this.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getPersistentDataContainer().has(entityKey, PersistentDataType.STRING)) {
                    entity.remove();
                }
            }
        }

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(
                AnimationManager.class,
                MenuManager.class,
                CrateManager.class
        );
    }

    public static EternalCrates get() {
        return instance;
    }

    public static NamespacedKey getEntityKey() {
        return entityKey;
    }

}
