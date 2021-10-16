package xyz.oribuin.eternalcrates;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Chicken;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.command.CrateCommand;
import xyz.oribuin.eternalcrates.listener.AnimationListeners;
import xyz.oribuin.eternalcrates.listener.CrateListeners;
import xyz.oribuin.eternalcrates.listener.PlayerListeners;
import xyz.oribuin.eternalcrates.manager.AnimationManager;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.eternalcrates.manager.MessageManager;
import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.orilibrary.util.NMSUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EternalCrates extends OriPlugin {

    private static EternalCrates instance;
    private List<UUID> activeUsers;

    @Override
    public void enablePlugin() {
        instance = this;

        // Make sure the server is using 1.16+
        if (NMSUtil.getVersionNumber() < 16) {
            this.getLogger().severe("You cannot use EternalCrates on 1." + NMSUtil.getVersionNumber() + ", We are limited to 1.16+");
            return;
        }

        this.activeUsers = new ArrayList<>();

        // Load Plugin Managers Asynchronously.
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.getManager(AnimationManager.class);
            this.getManager(CrateManager.class);
            this.getManager(DataManager.class);
            this.getManager(MessageManager.class);
        });

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new AnimationListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new CrateListeners(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

        // Register Plugin Command
        new CrateCommand(this);

    }

    @Override
    public void disablePlugin() {
        // Let's make sure there's no chickens left
        this.getServer().getWorlds().forEach(world -> world.getEntitiesByClasses(Chicken.class).forEach(chicken -> {
            if (chicken.getPersistentDataContainer().has(new NamespacedKey(this, "chicken"), PersistentDataType.INTEGER))
                chicken.remove();
        }));
    }


    public static EternalCrates getInstance() {
        return instance;
    }

    public List<UUID> getActiveUsers() {
        return activeUsers;
    }

}
