package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.persistence.PersistentDataType;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BunnyAnimation extends CustomAnimation {

    private int bunnyCount;
    private int duration;

    public BunnyAnimation() {
        super("bunny", "Oribuin", AnimationType.SEASONAL);
    }

    @Override
    public void spawn(Location location, Player player) {

        List<Rabbit> rabbits = this.spawnRabbits(location, this.bunnyCount);
        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> {
            rabbits.forEach(Rabbit::remove);
            this.getCrate().finish(player, location);
        }, this.duration * 20L);

    }

    // spawn a list rabbit with random age and color
    public List<Rabbit> spawnRabbits(Location location, int count) {
        List<Rabbit> rabbits = new ArrayList<>();

        final World world = location.getWorld();
        if (world == null)
            return rabbits;

        for (int i = 0; i <= count; i++) {
            Rabbit rabbit = world.spawn(location.clone().add(0, 1, 0), Rabbit.class, entity -> {
                entity.setRabbitType(this.getRandomRabbitType());
                entity.setInvulnerable(true);
                entity.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                entity.setCollidable(false);
            });

            rabbit.setVelocity(rabbit.getVelocity().multiply(0.5));
            rabbits.add(rabbit);
        }

        return rabbits;
    }

    // Get random Rabbit.Type value
    public Rabbit.Type getRandomRabbitType() {
        return Rabbit.Type.values()[(int) (Math.random() * Rabbit.Type.values().length)];
    }

    /**
     * Despawns a list of armorstands
     *
     * @param values list of armorstands
     */
    public void despawn(List<Rabbit> values) {
        Bukkit.getScheduler().runTaskLater(EternalCrates.getInstance(), () -> values.forEach(Rabbit::remove), 5 * 20);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return Map.of("animation.bunny.count", 15, "animation.duration", 10);
    }

    @Override
    public void load() {
        this.bunnyCount = this.get("animation.bunny.count", 15);
        this.duration = this.get("animation.duration", 10);
    }
}
