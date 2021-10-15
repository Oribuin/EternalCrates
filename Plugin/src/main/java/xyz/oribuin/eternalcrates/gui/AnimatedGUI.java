package xyz.oribuin.eternalcrates.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.GuiAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.gui.Gui;
import xyz.oribuin.orilibrary.util.HexUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimatedGUI {

    public AnimatedGUI(final EternalCrates plugin, final Crate crate, final Player player) {

        if (!(crate.getAnimation() instanceof GuiAnimation animation)) {
            return;
        }

        final Gui gui = new Gui(animation.getGuiSize(), HexUtils.colorify(crate.getDisplayName()));
        gui.setDefaultClickFunction(e -> {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        });

        gui.setPersonalClickAction(e -> gui.getDefaultClickFunction().accept(e));

        Reward temp = null;

        final List<Reward> rewards = new ArrayList<>(crate.getRewardMap().keySet());
        Collections.shuffle(rewards);

        // Add any items missing to make sure we can fill out the slots
        for (int i = 0; rewards.size() < animation.getRotateSlots().length; i++) {
            if (rewards.size() == 0)
                return;

            rewards.add(rewards.get(i));
        }

        Collections.shuffle(rewards);

        // Select a reward.
        Reward finalReward = crate.selectReward();

        // no not perfect and no I don't like it, but we're dealing with it
        AtomicInteger rotationCount = new AtomicInteger();
        Bukkit.getScheduler().runTaskTimer(plugin, (baseTask) -> {

            final Reward rotatedReward = animation.rotateItems(gui, rewards);
            animation.getSpinConsumer().accept(player, gui);

            if (rotatedReward == finalReward)
                rotationCount.getAndIncrement();

            if (rotationCount.get() == animation.getRotationCount()) {
                baseTask.cancel();

                if (finalReward == null)
                    return;

                animation.finishFunction(crate, finalReward, player);

                // close the inventory 2 second later, so they can see the reward they won
                Bukkit.getScheduler().runTaskLater(plugin, player::closeInventory, 40);
            }

        }, 0, 4);

        gui.open(player);
    }

}
