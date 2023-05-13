package xyz.oribuin.eternalcrates.particle;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class ParticleData {

    // General particle data
    private Particle particle;
    private Particle.DustOptions dustOptions;
    private Particle.DustTransition dustTransition;
    private BlockData materialData;
    private ItemStack itemStackData;
    private int note;

    public ParticleData(final Particle particle) {
        this.particle = particle;
        this.dustOptions = new DustOptions(Color.AQUA, 1);
        this.dustTransition = new DustTransition(Color.AQUA, Color.WHITE, 1);
        this.materialData = Material.BLACK_WOOL.createBlockData();
        this.itemStackData = new ItemStack(Material.BLACK_WOOL);
    }

    public void spawn(@Nullable Player player, Location loc, int count, double offsetX, double offsetY, double offsetZ) {

        final World world = loc.getWorld();
        if (world == null) return;

        AbstractParticleSpawner spawner = (player == null ? new WorldParticleSpawner(world) : new PlayerParticleSpawner(player));

        switch (this.particle.name()) {
            // Dust Particle.
            case "REDSTONE" -> spawner.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, this.dustOptions);
            case "DUST_COLOR_TRANSITION" -> spawner.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, this.dustTransition);

            // Spawn in Spell Mob Particles
            case "SPELL_MOB", "SPELL_MOB_AMBIENT" -> spawner.spawnParticle(this.particle, loc, 0, this.dustOptions.getColor().getRed() / 255.0, this.dustOptions.getColor().getGreen() / 255.0, this.dustOptions.getColor().getBlue() / 255.0, 1.0);
            case "BLOCK_CRACK", "BLOCK_DUST", "FALLING_DUST", "BLOCK_MARKER" -> spawner.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, 0, this.materialData);
            case "ITEM_CRACK" -> spawner.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, 0, this.itemStackData);
            case "NOTE" -> spawner.spawnParticle(this.particle, loc, 0, this.note / 24.0, 0, 0, 1);
            case "VIBRATION", "SKULK_CHARGE", "SHRIEK" -> {
                // fuck these particles
            }

            // Any other particle.
            default -> world.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, 0);
        }
    }

    public void spawn(@Nullable Player player, Location loc, int count) {
        this.spawn(player, loc, count, 0.0, 0.0, 0.0);
    }

    public Particle getParticle() {
        return particle;
    }

    public ParticleData setParticle(final Particle particle) {
        this.particle = particle;
        return this;
    }

    public DustOptions getDustOptions() {
        return dustOptions;
    }

    public ParticleData setDustOptions(final Color color) {
        if (color == null)
            return this;

        this.dustOptions = new DustOptions(color, 1f);
        return this;
    }

    public ParticleData setDustOptions(final DustOptions dustOptions) {
        if (dustOptions == null)
            return this;

        this.dustOptions = dustOptions;
        return this;
    }


    public DustTransition getDustTransition() {
        return dustTransition;
    }

    public ParticleData setDustTransition(final Color color1, final Color color2) {
        if (color1 == null || color2 == null)
            return this;

        this.dustTransition = new DustTransition(color1, color2, 1f);
        return this;
    }

    public ParticleData setDustTransition(final DustTransition dustTransition) {
        if (dustTransition == null)
            return this;

        this.dustTransition = dustTransition;
        return this;
    }

    public BlockData getMaterialData() {
        return materialData;
    }

    public ParticleData setMaterialData(final Material materialData) {
        if (materialData == null)
            return this;

        this.materialData = materialData.createBlockData();
        return this;
    }

    public ParticleData setMaterialData(final BlockData materialData) {
        this.materialData = materialData;
        return this;
    }

    public ItemStack getItemStackData() {
        return itemStackData;
    }

    public ParticleData setItemStackData(final Material materialData) {
        if (materialData == null)
            return this;

        this.itemStackData = new ItemStack(materialData);
        return this;
    }

    public ParticleData setItemStackData(final ItemStack itemStackData) {
        if (itemStackData == null)
            return this;

        this.itemStackData = itemStackData;
        return this;
    }


    public int getNote() {
        return note;
    }

    public ParticleData setNote(final int note) {
        this.note = note;
        return this;
    }


}
