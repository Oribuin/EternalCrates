package xyz.oribuin.eternalcrates.manager;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.action.*;
import xyz.oribuin.eternalcrates.animation.Animation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.crate.Reward;
import xyz.oribuin.eternalcrates.util.PluginUtils;
import xyz.oribuin.gui.Item;
import xyz.oribuin.orilibrary.manager.Manager;
import xyz.oribuin.orilibrary.util.HexUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CrateManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final AnimationManager animationManager = this.plugin.getManager(AnimationManager.class);

    private final Map<String, Crate> cachedCrates = new HashMap<>();

    public CrateManager(EternalCrates plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        this.plugin.getLogger().info("Loading all the crates from the plugin.");

        this.cachedCrates.clear();
        final File folder = new File(this.plugin.getDataFolder(), "crates");
        if (!folder.exists()) {
            folder.mkdir();
            PluginUtils.createDefaultFiles(plugin);
        }

        if (folder.listFiles() == null)
            return;

        Arrays.stream(folder.listFiles())
                .filter(file -> file.getName().toLowerCase().endsWith(".yml"))
                .forEach(file -> {
                    final Crate crate = this.createCreate(YamlConfiguration.loadConfiguration(file));
                    if (crate == null)
                        return;

                    this.cachedCrates.put(crate.getId(), crate);
                    this.plugin.getLogger().info("Registered Crate: " + crate.getId() + " with " + crate.getRewardMap().size() + " rewards!");
                });
    }

    /**
     * Get a crate from the id of the crate.
     *
     * @param id The ID of the crate.
     * @return An Optional Crate
     */
    public Optional<Crate> getCrate(String id) {
        return this.cachedCrates.values()
                .stream()
                .filter(crate -> crate.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    /**
     * Create a creation from a file configuration
     *
     * @param config The config the crate is being created from
     * @return The crate.
     */
    public Crate createCreate(final FileConfiguration config) {
        final Optional<String> name = Optional.ofNullable(config.getString("name"));
        if (name.isEmpty())
            return null;

        final Optional<String> displayName = Optional.ofNullable(config.getString("display-name"));
        if (displayName.isEmpty())
            return null;

        // this line isn't dumb
        final Optional<? extends Animation> animation = animationManager.getAnimationFromConfig(config);
        if (animation.isEmpty())
            return null;

        final List<Reward> rewards = new ArrayList<>();
        final ConfigurationSection section = config.getConfigurationSection("rewards");
        if (section == null)
            return null;

        final AtomicInteger id = new AtomicInteger(0);
        section.getKeys(false).forEach(s -> {
            final ItemStack item = this.itemFromSection(section, s);
            if (item == null)
                return;

            final Reward reward = new Reward(id.getAndIncrement());
            reward.setDisplayItem(item);
            reward.setChance(section.getInt(s + ".chance"));
            // section inception
            // holup that rhymes
            final ConfigurationSection commandSection = section.getConfigurationSection(s + ".actions");
            if (commandSection != null) {
                final List<Action> actions = Arrays.asList(
                        new BroadcastAction(),
                        new CloseAction(),
                        new ConsoleAction(),
                        new MessageAction(),
                        new PlayerAction(),
                        new SoundAction()
                );

                commandSection.getKeys(false).forEach(i -> {

                    Optional<Action> optional = actions.stream().filter(x -> {
                        final String formattedAction = "[" + x.actionType().toLowerCase() + "]";
                        return formattedAction.startsWith(i.toLowerCase());
                    }).findFirst();

                    if (optional.isEmpty())
                        return;

                    Action action = optional.get();
                    final String formattedAction = "[" + action.actionType().toLowerCase() + "]";

                    action.setMessage(i.substring(formattedAction.length()));
                    reward.getActions().add(action);

                });
            }

            rewards.add(reward);
        });

        final Crate crate = new Crate(name.get());
        crate.setAnimation(animation.get());
        crate.setDisplayName(displayName.get());
        rewards.forEach(reward -> crate.getRewardMap().put(reward, reward.getChance()));


        return crate;
    }


    // Avert your eyes, please

    /**
     * Create an ItemStack from a config section
     *
     * @param section The config section the item is from.
     * @param key     The key to the item.
     * @return The new ly created itemstack.
     */
    public ItemStack itemFromSection(final ConfigurationSection section, final String key) {
        final String materialName = section.getString(key + ".material");
        if (materialName == null)
            return null;

        final Material material = Material.matchMaterial(materialName.toUpperCase());

        if (material == null || !material.isItem())
            return null;

        // Yes I am aware this is a mess, I hate it too im sorry
        final Item.Builder itemBuilder = new Item.Builder(material)
                .setName(HexUtils.colorify(this.get(section, key + ".name", "&cInvalid Name")))
                .setLore(this.get(section, key + ".lore", new ArrayList<String>()).stream().map(HexUtils::colorify).collect(Collectors.toList()))
                .setAmount(Math.max(this.get(section, key + ".amount", 1), 1))
                .glow(this.get(section, key + ".glow", true))
                .setTexture(this.get(section, key + ".texture", ""))
                .setOwner(Bukkit.getOfflinePlayer(UUID.fromString(this.get(section, key + ".owner", ""))));

        // Add any enchantments
        final ConfigurationSection enchants = section.getConfigurationSection(key + ".enchants");
        if (enchants != null)
            enchants.getKeys(false).forEach(s -> {

                // Get enchantment by name
                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(s.toLowerCase()));
                if (enchantment == null) {
                    return;
                }

                // Add enchantment to item
                itemBuilder.addEnchant(enchantment, this.get(enchants, s, 1));
            });

        ItemStack item = itemBuilder.create();
        // Add any nbt tags somehow, I pray this works.
        final ConfigurationSection nbt = section.getConfigurationSection(key + ".nbt");
        if (nbt != null) {
            for (String s : nbt.getKeys(false))
                item = NBTEditor.set(item, nbt.get(s), s);
        }

        return item;
    }

    /**
     * Get a value from a configuration file.
     *
     * @param config The file configuration
     * @param path   The path to the option.
     * @param def    The default value for the option.
     * @return The config option or the default.
     */
    public <T> T get(FileConfiguration config, String path, T def) {
        return config.get(path) != null ? (T) config.get(path) : def;
    }

    /**
     * Get a value from a configuration section.
     *
     * @param section The configuration section
     * @param path    The path to the option.
     * @param def     The default value for the option.
     * @return The config option or the default.
     */
    public <T> T get(ConfigurationSection section, String path, T def) {
        return section.get(path) != null ? (T) section.get(path) : def;
    }

    public Map<String, Crate> getCachedCrates() {
        return cachedCrates;
    }

}
