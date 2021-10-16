package xyz.oribuin.eternalcrates.manager;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.animation.*;
import xyz.oribuin.eternalcrates.animation.defaults.*;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.gui.Item;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.HexUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AnimationManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final Map<String, Animation> cachedAnimations = new HashMap<>();

    public AnimationManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        this.plugin.getLogger().info("Loading all the animations for the plugin.");

        // Add the default

        // GUI Animations
        this.cachedAnimations.put("csgo", new CsgoAnimation());
        this.cachedAnimations.put("wheel", new WheelAnimation());
        // Particle Animations
        this.cachedAnimations.put("rings", new RingsAnimation());
        // Firework Particles
        this.cachedAnimations.put("sparkle", new SparkleAnimation()); // we may need a better name for this.
        this.cachedAnimations.put("celebration", new CelebrationAnimation());
        // Custom
        this.cachedAnimations.put("chicken", new ChickenAnimation());
        this.cachedAnimations.put("fountain", new FountainAnimation());
        // Hologram

        // Other
        this.cachedAnimations.put("none", new EmptyAnimation());
    }

    /**
     * Get an animation from the name.
     *
     * @param name The name of the animation
     * @return An optional animation.
     */
    public Optional<Animation> getAnimation(String name) {
        return this.cachedAnimations.values().stream()
                .filter(animation -> animation.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Get an animation from the config;
     *
     * @param config The configuration file
     * @return The Optional Animation Type.
     */
    public Optional<? extends Animation> getAnimationFromConfig(final FileConfiguration config) {

        // get the base optional animation.
        final Optional<Animation> optional = this.getAnimation(config.getString("animation.name"));
        if (optional.isEmpty())
            return Optional.empty();

        switch (optional.get().getAnimationType()) {
            case PARTICLES -> {
                return getParticleAni(config, optional.get());
            }
            case GUI -> {
                return getGuiAnimation(config, optional.get());
            }
            case FIREWORKS -> {
                return Optional.of((FireworkAnimation) optional.get());
            }
            case CUSTOM -> {
                return Optional.of((CustomAnimation) optional.get());
            }
            case NONE -> {
                return Optional.of((EmptyAnimation) optional.get());
            }
            case HOLOGRAM -> {
            }

        }

        return Optional.empty();
    }

    private Optional<ParticleAnimation> getParticleAni(final FileConfiguration config, final Animation animation) {
        if (!(animation instanceof ParticleAnimation particleAni))
            return Optional.empty();

        Particle particle = Arrays.stream(Particle.values()).filter(x -> x.name().equalsIgnoreCase(config.getString("animation.particle")))
                .findFirst()
                .orElse(Particle.FLAME);

        final ParticleData particleData = new ParticleData(particle);
        particleData.setDustColor(PluginUtils.fromHex(PluginUtils.get(config, "animation.color", "#FFFFFF")));
        particleData.setTransitionColor(PluginUtils.fromHex(PluginUtils.get(config, "animation.transition", "#ff0000")));
        particleData.setNote(PluginUtils.get(config, "animation.note", 1));
        particleData.setItemMaterial(Material.matchMaterial(PluginUtils.get(config, "animation.item", "DIRT")));
        particleData.setBlockMaterial(Material.matchMaterial(PluginUtils.get(config, "animation.block", "BLOCK")));

        particleAni.setParticleData(particleData);
        return Optional.of(particleAni);
    }

    /**
     * Get a gui animation values from the config.
     *
     * @param config    The file configuration
     * @param animation The base animation
     * @return The GUI Animation.
     */
    public Optional<GuiAnimation> getGuiAnimation(FileConfiguration config, Animation animation) {
        if (!(animation instanceof GuiAnimation gui))
            return Optional.empty();

        final ItemStack item = itemFromSection(config, "animation.filler-item");

        gui.setFillerItem(item);

        final ConfigurationSection section = config.getConfigurationSection("animation.extras");
        if (section == null)
            return Optional.of(gui);

        // Add all the extra items to the gui
        section.getKeys(false).forEach(s -> {
            ItemStack extraItem = itemFromSection(config, "animation.extras." + s);
            int slot = PluginUtils.get(config, "animation.extras." + s + ".slot", 1);
            gui.getExtras().put(slot, extraItem);
        });

        return Optional.of(gui);
    }

    /**
     * Get a list of animations from the animation type
     *
     * @param type The animation type.
     * @return The list of animations.
     */
    public List<Animation> getAnimationFromType(AnimationType type) {
        return this.cachedAnimations.values().stream()
                .filter(animation -> animation.getAnimationType() == type)
                .collect(Collectors.toList());
    }

    public Map<String, Animation> getCachedAnimations() {
        return cachedAnimations;
    }

    /**
     * Register an animation into the plugin.
     *
     * @param animation The animation being registered.
     */
    public static void registerAnimation(Animation animation) {
        EternalCrates.getInstance().getLogger().info("Registered Crate Animation: " + animation.getName());
        EternalCrates.getInstance().getManager(AnimationManager.class).getCachedAnimations().put(animation.getName(), animation);
    }

    /**
     * Create an ItemStack from a config section, Please avert your eyes from this monstrosity.
     *
     * @param section The config section the item is from.
     * @param path    The path to the item.
     * @return The new ly created itemstack.
     */
    public ItemStack itemFromSection(final FileConfiguration section, final String path) {
        final String materialName = section.getString(path + ".material");
        if (materialName == null)
            return null;

        final Material material = Material.matchMaterial(materialName.toUpperCase());

        if (material == null || !material.isItem())
            return null;

        // Yes I am aware this is a mess, I hate it too im sorry
        final Item.Builder itemBuilder = new Item.Builder(material)
                .setName(HexUtils.colorify(PluginUtils.get(section, path + ".name", null)))
                .setLore(PluginUtils.get(section, path + ".lore", new ArrayList<String>()).stream().map(HexUtils::colorify).collect(Collectors.toList()))
                .setAmount(Math.max(PluginUtils.get(section, path + ".amount", 1), 1))
                .glow(PluginUtils.get(section, path + ".glow", false))
                .setTexture(PluginUtils.get(section, path + ".texture", null));

        if (section.get(path + ".owner") != null)
            itemBuilder.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(PluginUtils.get(section, path + ".owner", null))));

        // Add any enchantments
        final ConfigurationSection enchants = section.getConfigurationSection(path + ".enchants");
        if (enchants != null)
            enchants.getKeys(false).forEach(s -> {

                // Get enchantment by name
                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(s.toLowerCase()));
                if (enchantment == null) {
                    return;
                }

                // Add enchantment to item
                itemBuilder.addEnchant(enchantment, PluginUtils.get(enchants, s, 1));
            });

        ItemStack item = itemBuilder.create();
        // Add any nbt tags somehow, I pray this works.
        final ConfigurationSection nbt = section.getConfigurationSection(path + ".nbt");
        if (nbt != null) {
            for (String s : nbt.getKeys(false))
                item = NBTEditor.set(item, nbt.get(s), s);
        }

        return item;
    }

}
