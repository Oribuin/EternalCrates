package xyz.oribuin.eternalcrates.animation.defaults;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class FountainAnimation extends Animation {

    private int itemCount;

    public FountainAnimation() {
        super("Fountain", "Oribuin");
    }

    @Override
    public void play(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        final World world = location.getWorld();
        if (world == null)
            return;

        final List<Reward> rewards = crate.createRewards();
        this.setActive(true);

        final List<Item> items = new ArrayList<>();
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        rewards.forEach(reward -> {
            crate.finish(player, rewards, location);

            for (int i = 0; i < rewards.size() * this.itemCount; i++) {
                Item item = world.dropItem(location.clone(), reward.getPreviewItem() , x -> {
                    x.setPickupDelay(Integer.MAX_VALUE);
                    x.setInvulnerable(true);
                    x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                    x.setCustomNameVisible(true);
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

    @Override
    public Map<String, Object> getRequiredValues() {
        return new LinkedHashMap<>() {{
            this.put("item-count", 1);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.itemCount = config.getInt("item-count", 10);
    }
}
