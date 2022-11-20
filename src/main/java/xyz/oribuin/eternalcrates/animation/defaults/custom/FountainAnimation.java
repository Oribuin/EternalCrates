package xyz.oribuin.eternalcrates.animation.defaults.custom;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class FountainAnimation extends CustomAnimation {

    private int itemCount;

    public FountainAnimation() {
        super("Fountain", "Oribuin", AnimationType.CUSTOM);
    }

    @Override
    public void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        final var world = location.getWorld();
        if (world == null)
            return;

        final var rewards = crate.createRewards();
        this.setActive(true);

        final List<Item> items = new ArrayList<>();
        final var random = ThreadLocalRandom.current();

        rewards.forEach(reward -> {
            crate.finish(player, rewards, location);

            for (int i = 0; i < rewards.size() * this.itemCount; i++) {
                var item = world.spawn(location.clone(), Item.class, x -> {
                    x.setItemStack(reward.getItemStack());
                    x.setPickupDelay(Integer.MAX_VALUE);
                    x.setInvulnerable(true);
                    x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                });

                var vectorX = random.nextDouble(-0.2, 0.2);
                var vectorY = random.nextDouble(0.5);
                var vectorZ = random.nextDouble(-0.2, 0.2);

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
        return new HashMap<>() {{
            this.put("item-count", 1);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.itemCount = config.getInt("crate-settings.animation.item-count", 10);
    }
}
