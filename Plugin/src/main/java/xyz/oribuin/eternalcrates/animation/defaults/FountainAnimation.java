package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FountainAnimation extends CustomAnimation {

    public FountainAnimation() {
        super("fountain", "Oribuin", AnimationType.CUSTOM);
    }

    @Override
    public void spawn(Crate crate, Location location, Player player) {
        final World world = location.getWorld();
        if (world == null)
            return;

        final List<Reward> rewards = crate.createRewards();
        this.setActive(true);

        final List<Item> items = new ArrayList<>();
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        rewards.forEach(reward -> {
            crate.finish(player, rewards);

            for (int i = 0; i < rewards.size() * 10; i++) {
                Item item = world.spawn(location.clone(), Item.class, x -> {
                    x.setItemStack(reward.getDisplayItem());
                    x.setPickupDelay(Integer.MAX_VALUE);
                    x.setInvulnerable(true);
                    x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                });

                double vectorX = random.nextDouble(-0.2, 0.2);
                double vectorY = random.nextDouble(0.5);
                double vectorZ = random.nextDouble(-0.2, 0.2);

                item.setVelocity(item.getVelocity().clone().add(new Vector(vectorX, vectorY, vectorZ)));
                items.add(item);
            }
        });

        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), x -> {
            this.setActive(false);
            items.forEach(Item::remove);
        }, 60);
    }

}
