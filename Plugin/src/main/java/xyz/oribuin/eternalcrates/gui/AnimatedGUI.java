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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

        // Add filler items to the gui
        if (animation.getFillerItem() != null) {
            for (int i = 0; i < animation.getGuiSize(); i++)
                gui.setItem(i, animation.getFillerItem(), event -> {
                });
        }

        // Add extra items to the gui
        animation.getExtras().entrySet().stream()
                .filter(entry -> Arrays.stream(animation.getRotateSlots()).noneMatch(value -> value == entry.getKey()))
                .forEach(entry -> gui.setItem(entry.getKey(), entry.getValue(), e -> {
                }));

        // Select a reward.
        final List<Reward> wonRewards = crate.createRewards();
        Reward finalReward = wonRewards.get(0);
        List<Reward> rewards = new ArrayList<>();

        // We add the reward first so that it doesnt spin indefinitely
        rewards.add(finalReward);
        rewards.addAll(crate.getRewardMap().values());

        // Add any items missing to make sure we can fill out the slots
        for (int i = 0; rewards.size() < animation.getRotateSlots().length; i++) {
            if (rewards.size() == 0)
                return;

            rewards.add(rewards.get(i));
        }

        Collections.shuffle(rewards);

        // This method fixes infinite loops, yes its ugly as shit
        if (rewards.stream().limit(animation.getRotateSlots().length).noneMatch(x -> x == finalReward)) {
            rewards.set(0, finalReward);
            rewards = rewards.stream().limit(animation.getRotateSlots().length).collect(Collectors.toList());
            Collections.shuffle(rewards);
        }

        // no not perfect and no I don't like it, but we're dealing with it
        AtomicInteger rotationCount = new AtomicInteger();
        List<Reward> finalRewards = rewards;
        Bukkit.getScheduler().runTaskTimer(plugin, (baseTask) -> {

            final Reward rotatedReward = animation.rotateItems(gui, finalRewards);
            animation.getSpinConsumer().accept(player, gui);

            if (rotatedReward.getId() == finalReward.getId())
                rotationCount.getAndIncrement();


            if (rotationCount.get() == animation.getRotationCount()) {
                baseTask.cancel();
                crate.finish(player, wonRewards);
            }

        }, 0, 4);

        gui.open(player);
    }

}
