package xyz.oribuin.eternalcrates;

import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.listener.PlayerListeners;
import xyz.oribuin.eternalcrates.manager.AnimationManager;
import xyz.oribuin.eternalcrates.manager.CrateManager;
import xyz.oribuin.eternalcrates.manager.DataManager;
import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.orilibrary.util.NMSUtil;

import java.util.HashSet;
import java.util.Set;

public class EternalCrates extends OriPlugin {

    private static EternalCrates instance;

    @Override
    public void enablePlugin() {
        instance = this;

        // Make sure the server is using 1.16+
        if (NMSUtil.getVersionNumber() < 16) {
            this.getLogger().severe("You cannot use EternalCrates on 1." + NMSUtil.getVersionNumber() + ", We are limited to 1.16+");
            return;
        }

        // Load Plugin Managers Asynchronously.
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.getManager(DataManager.class);
            this.getManager(AnimationManager.class);
            this.getManager(CrateManager.class);
        });

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

        // Register Plugin Commands
        // TODO

    }

    @Override
    public void disablePlugin() {

    }


    public static EternalCrates getInstance() {
        return instance;
    }

}
