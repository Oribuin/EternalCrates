package xyz.oribuin.eternalcrates;

import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.listener.PlayerListeners;
import xyz.oribuin.orilibrary.OriPlugin;
import xyz.oribuin.orilibrary.util.NMSUtil;

import java.util.HashSet;
import java.util.Set;

public class EternalCrates extends OriPlugin {

    private static EternalCrates instance;
    private final Set<Animation> animationSet = new HashSet<>();

    @Override
    public void enablePlugin() {

        // Make sure the server is using 1.16+
        if (NMSUtil.getVersionNumber() < 16) {
            this.getLogger().severe("You cannot use EternalCrates on 1." + NMSUtil.getVersionNumber() + ", We are limited to 1.16+");
            return;
        }

        instance = this;

        // Load Plugin Managers Asynchronously.
        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            // TODO
        });

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);

        // Register Plugin Commands
        // TODO

    }

    @Override
    public void disablePlugin() {

    }

    public Set<Animation> getAnimationSet() {
        return animationSet;
    }

    /**
     * Register an animation into the plugin.
     *
     * @param animation The animation being registered.
     */
    public static void registerAnimation(Animation animation) {
        instance.getLogger().info("Registered Crate Animation: " + animation.getName());
        instance.animationSet.add(animation);
    }

    public static EternalCrates getInstance() {
        return instance;
    }

}
