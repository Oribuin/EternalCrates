package xyz.oribuin.eternalcrates.manager;

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
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.nms.NMSAdapter;
import xyz.oribuin.eternalcrates.nms.NMSHandler;
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

    /**
     * Register an animation into the plugin.
     *
     * @param animation The animation being registered.
     */
    public static void register(Animation animation) {
        final EternalCrates plugin = EternalCrates.getInstance();
        plugin.getLogger().info("Registered Crate Animation: " + animation.getName());
        plugin.getManager(AnimationManager.class).getCachedAnimations().put(animation.getName(), animation);

        final CrateManager crateManager = plugin.getManager(CrateManager.class);

        // Recreate any crates that weren't registered as animations.
        new HashSet<>(crateManager.getUnregisteredCrates().entrySet()).forEach(x -> {
            crateManager.getUnregisteredCrates().remove(x.getKey());

            Crate crate = crateManager.createCreate(x.getValue());
            if (crate != null) {
                crateManager.getCachedCrates().put(crate.getId(), crate);
            }
        });
    }

    @Override
    public void enable() {
        this.plugin.getLogger().info("Loading all the animations for the plugin.");

        // Add the default animations
        // GUI Animations
        this.cachedAnimations.put("csgo", new CsgoAnimation());
        this.cachedAnimations.put("wheel", new WheelAnimation());
        // Particle Animations
        this.cachedAnimations.put("rings", new RingsAnimation());
        this.cachedAnimations.put("ripple", new RippleAnimation());
        this.cachedAnimations.put("quad", new QuadAnimation());
        // Firework Particles
        this.cachedAnimations.put("sparkle", new SparkleAnimation()); // we may need a better name for this.
        this.cachedAnimations.put("celebration", new CelebrationAnimation());
        // Custom
        this.cachedAnimations.put("chicken", new ChickenAnimation());
        this.cachedAnimations.put("fountain", new FountainAnimation());
        // Seasonal
        this.cachedAnimations.put("snowman", new SnowmanAnimation());
        this.cachedAnimations.put("pumpkin", new PumpkinAnimation());
        // Hologram
        // TODO
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

        // get the base animation.
        Optional<? extends Animation> animation = this.getAnimation(config.getString("animation.name"));
        if (animation.isEmpty())
            return Optional.empty();

        switch (animation.get().getAnimationType()) {
            case PARTICLES -> animation = getParticleAni(config, animation.get());
            case GUI -> animation = getGuiAnimation(config, animation.get());
            case FIREWORKS -> animation = Optional.of((FireworkAnimation) animation.get());
            case CUSTOM -> animation = Optional.of((CustomAnimation) animation.get());
            case NONE -> animation = Optional.of((EmptyAnimation) animation.get());
        }

        return animation;
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

        final ItemStack item = itemFromConfig(config, "animation.filler-item");

        gui.setFillerItem(item);

        final ConfigurationSection section = config.getConfigurationSection("animation.extras");
        if (section == null)
            return Optional.of(gui);

        // Add all the extra items to the gui
        section.getKeys(false).forEach(s -> {
            ItemStack extraItem = itemFromConfig(config, "animation.extras." + s);
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
     * Create an ItemStack from a config  Please avert your eyes from this monstrosity.
     *
     * @param config The config the item is from.
     * @param path   The path to the item.
     * @return The new ly created itemstack.
     */
    public ItemStack itemFromConfig(final FileConfiguration config, final String path) {
        final String materialName = config.getString(path + ".material");
        if (materialName == null)
            return null;

        final Material material = Material.matchMaterial(materialName.toUpperCase());

        if (material == null || !material.isItem())
            return null;

        // Yes I am aware this is a mess, I hate it too im sorry
        final Item.Builder itemBuilder = new Item.Builder(material)
                .setName(HexUtils.colorify(PluginUtils.get(config, path + ".name", null)))
                .setLore(PluginUtils.get(config, path + ".lore", new ArrayList<String>()).stream().map(HexUtils::colorify).collect(Collectors.toList()))
                .setAmount(Math.max(PluginUtils.get(config, path + ".amount", 1), 1))
                .glow(PluginUtils.get(config, path + ".glow", false))
                .setTexture(PluginUtils.get(config, path + ".texture", null));

        if (config.get(path + ".owner") != null)
            itemBuilder.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(PluginUtils.get(config, path + ".owner", null))));

        // Add any enchantments
        final ConfigurationSection enchants = config.getConfigurationSection(path + ".enchants");
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
        final ConfigurationSection nbt = config.getConfigurationSection(path + ".nbt");
        if (nbt != null) {
            NMSHandler handler = NMSAdapter.getHandler();

            for (String s : nbt.getKeys(false)) {
                Object obj = nbt.get(s);

                // this is a goddamn sin, I hate this
                if (obj instanceof String)
                    item = handler.setString(item, s, nbt.getString(s));

                // you've coded for 3 years and can't do it any better?
                if (obj instanceof Long)
                    item = handler.setLong(item, s, nbt.getLong(s));

                // lord no
                if (obj instanceof Integer)
                    item = handler.setInt(item, s, nbt.getInt(s));

                // please make it stop
                if (obj instanceof Boolean)
                    item = handler.setBoolean(item, s, nbt.getBoolean(s));

                // goddamn
                if (obj instanceof Double)
                    item = handler.setDouble(item, s, nbt.getDouble(s));

                // thank god its over
            }
        }

        return item;
    }

}
