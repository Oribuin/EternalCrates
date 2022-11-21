package xyz.oribuin.eternalcrates.animation;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FireworkAnimation extends Animation {

    private final Map<Integer, CustomFirework> fireworkMap = new HashMap<>();

    public FireworkAnimation() {
        super("General", AnimationType.CUSTOM, "Oribuin", true);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new HashMap<>() {{

            // First Firework Effect
            this.put("1.offset-x", 0.0);
            this.put("1.offset-y", 0.0);
            this.put("1.offset-z", 0.0);
            this.put("1.fire-type", 0.0);
            this.put("1.detonation-delay", 0.0);
            this.put("1.power", 0.0);

            // First Effect
            this.put("1.effects.1.colors", List.of("#FFFFFF"));
            this.put("1.effects.1.fade-colors", List.of("#000000"));
            this.put("1.effects.1.flicker", true);
            this.put("1.effects.1.trail", true);
            this.put("1.effects.1.type", "BALL_LARGE");

            // Second Firework Effect
            this.put("2.offset-x", 0.0);
            this.put("2.offset-y", 0.0);
            this.put("2.offset-z", 0.0);
            this.put("2.fire-type", "0");
            this.put("2.detonation-delay", 3.0);
            this.put("2.power", 3.0);

            // Second Effect
            this.put("2.effects.1.colors", List.of("#FFFFFF"));
            this.put("2.effects.1.fade-colors", List.of("#000000"));
            this.put("2.effects.1.flicker", true);
            this.put("2.effects.1.trail", true);
            this.put("2.effects.1.type", "BALL_LARGE");
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {

        CommentedConfigurationSection section = config.getConfigurationSection("crate-settings.animation.firework-settings");
        if (section == null)
            return;

        this.fireworkMap.clear();
        for (String key : section.getKeys(false)) {
            CustomFirework firework = new CustomFirework();

            // Set the basic firework settings
            firework.setOffsetX(section.getDouble(key + ".offset-x"));
            firework.setOffsetY(section.getDouble(key + ".offset-y"));
            firework.setOffsetZ(section.getDouble(key + ".offset-z"));
            firework.setDetonationDelay(section.getDouble(key + ".detonation-delay"));
            firework.setFireDelay(section.getDouble(key + ".fire-delay"));
            firework.setPower(Math.min(Math.max(section.getInt(key + ".power"), 0), 127));

            // Build the firework effect
            CommentedConfigurationSection effectSection = section.getConfigurationSection(key + ".effects");
            if (effectSection == null)
                continue;

            for (String effectKey : effectSection.getKeys(false)) {
                FireworkEffect.Builder builder = FireworkEffect.builder();
                effectSection.getStringList(effectKey + ".colors").forEach(colorCode -> {
                    Color color = PluginUtils.fromHex(colorCode);
                    if (color != null)
                        builder.withColor(color);

                });

                // Load the firework fade colors
                effectSection.getStringList(effectKey + ".fade-colors").forEach(colorCode -> {
                    Color color = PluginUtils.fromHex(colorCode);
                    if (color != null)
                        builder.withFade(color);
                });


                // Load the firework type
                FireworkEffect.Type type = null;
                String typeString = effectSection.getString(effectKey + ".type");
                for (FireworkEffect.Type t : FireworkEffect.Type.values()) {
                    if (t.name().equalsIgnoreCase(typeString)) {
                        type = t;
                        break;
                    }
                }

                builder.with(type == null ? FireworkEffect.Type.BALL : type);

                // Load the firework flicker
                builder.flicker(effectSection.getBoolean(effectKey + ".flicker"));

                // Load the firework trail
                builder.trail(effectSection.getBoolean(effectKey + ".trail"));

                // add effect to firework
                firework.getEffects().add(builder.build());
            }

            this.fireworkMap.put(new AtomicInteger(this.fireworkMap.size()).incrementAndGet(), firework);
        }
    }

    /**
     * Play the animation at the crate location
     *
     * @param loc    The location of the fireworks.
     * @param player The player who is opening the crate.
     */
    public void play(@NotNull Location loc, @NotNull Player player, @NotNull Crate crate) {
        if (this.isActive())
            return;

        this.setActive(true);

        double startTime = System.currentTimeMillis();
        var world = loc.getWorld();
        if (world == null)
            return;

        Map<Integer, CustomFirework> newFireworkMap = new HashMap<>(this.fireworkMap);

        Bukkit.getScheduler().runTaskTimer(EternalCrates.getInstance(), task -> {
            for (var entry : newFireworkMap.entrySet()) {
                var fireworkData = entry.getValue();

                // Check the delay between each firework,
                if (fireworkData.getFireDelay() != 0 && System.currentTimeMillis() - startTime > fireworkData.getFireDelay() * 1000) {
                    return;
                }

                // Remove firework from map
                newFireworkMap.remove(entry.getKey());

                // Spawn the firework
                var newLocation = loc.clone().add(fireworkData.getOffsetX(), fireworkData.getOffsetY(), fireworkData.getOffsetZ());
                var firework = world.spawn(newLocation.clone(), Firework.class, x -> {
                    final var meta = x.getFireworkMeta();

                    meta.addEffects(fireworkData.getEffects());
                    meta.setPower(fireworkData.getPower());
                    meta.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                    x.setFireworkMeta(meta);
                });

                // Detonate the firework
                if (fireworkData.getDetonationDelay() != 0 && System.currentTimeMillis() - startTime > fireworkData.getDetonationDelay() * 1000) {
                    firework.detonate();
                }
            }

            if (newFireworkMap.isEmpty()) {
                crate.finish(player, loc);
                task.cancel();
                this.setActive(false);
            }
        }, 0, 1);

    }

}
