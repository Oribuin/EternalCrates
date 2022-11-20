package xyz.oribuin.eternalcrates.animation.defaults.seasonal;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BunnyAnimation extends CustomAnimation {

    private int bunnyCount = 10;
    private int duration = 10;

    public BunnyAnimation() {
        super("Bunny", "Oribuin", AnimationType.SEASONAL);
    }

    @Override
    public void spawn(@NotNull Location location, @NotNull Player player, @NotNull Crate crate) {
        List<Rabbit> rabbits = new ArrayList<>();

        final var world = location.getWorld();
        if (world == null)
            return;

        for (int i = 0; i <= bunnyCount; i++) {
            var rabbit = world.spawn(location.clone().add(0, 1, 0), Rabbit.class, entity -> {
                entity.setRabbitType(this.getRandomRabbitType());
                entity.setInvulnerable(true);
                entity.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                entity.setCollidable(false);
            });

            rabbit.setVelocity(rabbit.getVelocity().multiply(0.5));
            rabbits.add(rabbit);
        }


        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
            rabbits.forEach(Rabbit::remove);
            crate.finish(player, location);
        }, this.duration * 20L);

    }

    // Get random Rabbit.Type value
    public Rabbit.Type getRandomRabbitType() {
        return Rabbit.Type.values()[(int) (Math.random() * Rabbit.Type.values().length)];
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{
            this.put("bunny-count", 10);
            this.put("duration", 10);
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {
        this.bunnyCount = config.getInt("animation.bunny.count");
        this.duration = config.getInt("animation.duration");
    }


}
