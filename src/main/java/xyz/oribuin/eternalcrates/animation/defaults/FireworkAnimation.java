package xyz.oribuin.eternalcrates.animation.defaults;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.animation.AnimationType;
import xyz.oribuin.eternalcrates.animation.CustomFirework;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.CrateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FireworkAnimation extends Animation {

    private final Map<Integer, CustomFirework> fireworkMap = new HashMap<>();

    public FireworkAnimation() {
        super("Fireworks", "Oribuin", AnimationType.MISC);
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        return new LinkedHashMap<>() {{

            // First Firework Effect
            this.put("firework-settings.1.offset-x", 0.0);
            this.put("firework-settings.1.offset-y", 1.0);
            this.put("firework-settings.1.offset-z", 0.0);
            this.put("firework-settings.1.fire-type", "0");
            this.put("firework-settings.1.detonation-delay", 0.0);
            this.put("firework-settings.1.fire-delay", 3.0);
            this.put("firework-settings.1.power", 0.0);

            // First Effect
            this.put("firework-settings.1.effects.1.colors", List.of("#FFFFFF"));
            this.put("firework-settings.1.effects.1.fade-colors", List.of("#000000"));
            this.put("firework-settings.1.effects.1.flicker", true);
            this.put("firework-settings.1.effects.1.trail", true);
            this.put("firework-settings.1.effects.1.type", "BALL");

            // Second Firework Effect
            this.put("firework-settings.2.offset-x", 0.0);
            this.put("firework-settings.2.offset-y", 0.0);
            this.put("firework-settings.2.offset-z", 0.0);
            this.put("firework-settings.2.fire-type", "0");
            this.put("firework-settings.2.detonation-delay", 3.0);
            this.put("firework-settings.2.fire-delay", 3.0);
            this.put("firework-settings.2.power", 0.0);

            // Second Effect
            this.put("firework-settings.2.effects.1.colors", List.of("#000000"));
            this.put("firework-settings.2.effects.1.fade-colors", List.of("#FF0000"));
            this.put("firework-settings.2.effects.1.flicker", true);
            this.put("firework-settings.2.effects.1.trail", true);
            this.put("firework-settings.2.effects.1.type", "BALL_LARGE");
        }};
    }

    @Override
    public void load(CommentedConfigurationSection config) {

        CommentedConfigurationSection section = config.getConfigurationSection("firework-settings");
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
                    Color color = CrateUtils.fromHex(colorCode);
                    if (color != null)
                        builder.withColor(color);

                });

                // Load the firework fade colors
                effectSection.getStringList(effectKey + ".fade-colors").forEach(colorCode -> {
                    Color color = CrateUtils.fromHex(colorCode);
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
        World world = loc.getWorld();
        if (world == null)
            return;

        Map<Integer, CustomFirework> newFireworkMap = new HashMap<>(this.fireworkMap);

        Bukkit.getScheduler().runTaskTimer(EternalCrates.getInstance(), task -> {
            // remove the firework from the map if it has been played
            List<Integer> remove = new ArrayList<>();

            for (Map.Entry<Integer, CustomFirework> entry : newFireworkMap.entrySet()) {
                CustomFirework fireworkData = entry.getValue();

                // Check the delay between each firework,
                double launchDelay = fireworkData.getFireDelay() * 1000;
                if (launchDelay != 0 && System.currentTimeMillis() - startTime < launchDelay) {
                    continue;
                }

                // Remove firework from map
                remove.add(entry.getKey());

                // Spawn the firework
                Location newLocation = loc.clone().add(fireworkData.getOffsetX(), fireworkData.getOffsetY(), fireworkData.getOffsetZ());
                Firework firework = world.spawn(newLocation.clone(), Firework.class, x -> {
                    final FireworkMeta meta = x.getFireworkMeta();

                    meta.addEffects(fireworkData.getEffects());
                    meta.setPower(fireworkData.getPower());
                    x.getPersistentDataContainer().set(EternalCrates.getEntityKey(), PersistentDataType.INTEGER, 1);
                    x.setFireworkMeta(meta);
                });

                // Detonate the firework
                double totalDelay = (fireworkData.getDetonationDelay() + fireworkData.getFireDelay()) * 1000;
                if (fireworkData.getDetonationDelay() == 0)
                    firework.detonate();

                else if (System.currentTimeMillis() - startTime > totalDelay)
                    firework.detonate();

            }

            // remove any of the fireworks from remove list from newFireworkMap
            remove.forEach(newFireworkMap::remove);

            // check if all the firework have been played
            if (newFireworkMap.isEmpty()) {
                crate.finish(player, loc);
                this.setActive(false);
                task.cancel();
            }

        }, 0, 1);

    }

}
