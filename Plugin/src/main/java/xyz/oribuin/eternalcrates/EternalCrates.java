package xyz.oribuin.eternalcrates;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.hook.PAPI;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EternalCrates extends RosePlugin {

    private static EternalCrates instance;
    private static NamespacedKey entityKey;
    private List<UUID> activeUsers;

    public EternalCrates() {
        super(97992, 14703, ConfigurationManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
        instance = this;
    }

    @Override
    public void enable() {
        // Make sure the server is using 1.16+
        if (NMSUtil.getVersionNumber() < 16) {
            this.getLogger().severe("You cannot use EternalCrates on 1." + NMSUtil.getVersionNumber() + ", We are limited to 1.16+");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;
        entityKey = new NamespacedKey(this, "entity");

        // Refresh active users list
        this.activeUsers = new ArrayList<>();

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new AnimationListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new CrateListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

        // Register PlaceholderAPI
        if (PAPI.isEnabled()) {
            new PAPI(this).register();
        }
    }

    @Override
    public void disable() {
        // Let's make sure there's no EternalCrates Entities left
        this.getServer().getWorlds().forEach(world -> world.getEntities().stream()
                .filter(entity -> entity.getPersistentDataContainer().has(entityKey, PersistentDataType.INTEGER))
                .forEach(Entity::remove));
    }

    @Override
    public void reload() {
        super.reload();
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> getManager(CrateManager.class).loadCrates());
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return Arrays.asList(AnimationManager.class, MenuManager.class, CrateManager.class);
    }

    public List<UUID> getActiveUsers() {
        return activeUsers;
    }

    public static EternalCrates getInstance() {
        return instance;
    }

    public static NamespacedKey getEntityKey() {
        return entityKey;
    }

}
