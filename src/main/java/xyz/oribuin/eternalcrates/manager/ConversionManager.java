package xyz.oribuin.eternalcrates.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ConversionManager extends Manager {

    private boolean hasConverted = false;

    public ConversionManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {

        if (!this.shouldConvert() || this.hasConverted) {
            return;
        }

        this.hasConverted = true;

        this.rosePlugin.getLogger().severe("Detected old EternalCrates configs. Converting to new format.");
        this.rosePlugin.getLogger().severe("This will move your old configurations to a backup folder and create new ones.");
        this.rosePlugin.getLogger().severe("If you have any issues, please contact the developer.");
        this.rosePlugin.getLogger().severe("Plugin will disable after conversion.");

        final File newFolder = new File(this.rosePlugin.getDataFolder(), "old-configurations");
        if (!newFolder.exists()) {
            newFolder.mkdir();
        }

        final File oldConfig = new File(this.rosePlugin.getDataFolder(), "config.yml");
        oldConfig.renameTo(new File(newFolder, "config.yml"));

        final File oldMessages = new File(this.rosePlugin.getDataFolder(), "messages.yml");
        oldMessages.renameTo(new File(newFolder, "messages.yml"));

        final File crateFolder = new File(this.rosePlugin.getDataFolder(), "crates");
        File[] crateFiles = crateFolder.listFiles();
        if (crateFiles != null) {
            crateFiles = Arrays.stream(crateFiles).filter(file -> file.getName().endsWith(".yml")).toArray(File[]::new);

            this.rosePlugin.getLogger().severe("Found " + crateFiles.length + " crates. Transferring...");
            var oldCrateConfigFolder = new File(newFolder, "crates");

            if (!oldCrateConfigFolder.exists()) {
                oldCrateConfigFolder.mkdir();
            }

            for (File file : crateFiles) {
                file.renameTo(new File(oldCrateConfigFolder, file.getName()));
            }
        }

        var data = this.rosePlugin.getManager(DataManager.class);
        data.dropTableMigration();
        data.recreateKeys();
        data.recreateLocations();

        this.rosePlugin.getLogger().severe("Converting complete. Disabling plugin, You will probably see an error soon");
        var manager = this.rosePlugin.getManager(CrateManager.class);
        manager.createExampleCrate(crateFolder);


        Bukkit.getPluginManager().disablePlugin(this.rosePlugin);
    }

    @Override
    public void disable() {

    }

    public boolean shouldConvert() {
        final var folder = new File(this.rosePlugin.getDataFolder(), "crates");
        final var messagesFile = new File(this.rosePlugin.getDataFolder(), "messages.yml");
        final var files = folder.listFiles();

        System.out.println(Arrays.toString(files));
        if (files == null)
            return false;


        boolean shouldConvert = false;

        var fileList = Arrays.stream(files)
                .filter(file -> file.getName().endsWith(".yml"))
                .collect(Collectors.toCollection(ArrayList::new));

        for (var file : fileList) {
            final var config = YamlConfiguration.loadConfiguration(file);
            if (!config.contains("crate-settings")) {
                shouldConvert = true;
                break;
            }
        }

        return shouldConvert && messagesFile.exists();
    }

}
