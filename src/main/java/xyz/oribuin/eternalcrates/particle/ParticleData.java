package xyz.oribuin.eternalcrates.particle;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class ParticleData {

    // General particle data
    private final Particle particle;
    private Color dustColor;
    private Color transitionColor;
    private int note;
    private Material itemMaterial;
    private Material blockMaterial;

    // Cached particle data
    private boolean isCached;
    private Particle.DustOptions dustOptions;
    private Particle.DustTransition dustTransition;
    private BlockData materialData;
    private ItemStack itemStackData;

    public ParticleData(final Particle particle) {
        this.particle = particle;
        this.dustColor = Color.AQUA;
        this.transitionColor = Color.WHITE;
        this.note = 0;
        this.itemMaterial = Material.DIRT;
        this.blockMaterial = Material.DIRT;

        this.isCached = false;
        this.dustOptions = null;
        this.dustTransition = null;
        this.materialData = null;
        this.itemStackData = null;
    }

    /**
     * Create the particle data for the player.
     */
    public ParticleData cacheParticleData() {
        this.dustOptions = new Particle.DustOptions(this.dustColor, 1f);
        this.dustTransition = new Particle.DustTransition(this.dustColor, this.transitionColor, 1f);
        this.materialData = (this.blockMaterial.isBlock() ? this.blockMaterial : Material.BLACK_WOOL).createBlockData();
        this.itemStackData = new ItemStack(this.itemMaterial.isItem() ? this.itemMaterial : Material.BLACK_WOOL);
        this.isCached = true;

        return this;
    }

    public void spawn(@Nullable Player player, Location loc, int count, double offsetX, double offsetY, double offsetZ) {

        final var world = loc.getWorld();
        if (world == null)
            return;

        if (!this.isCached) {
            this.cacheParticleData();
        }

        AbstractParticleSpawner spawner = (player == null ? new WorldParticleSpawner(world) : new PlayerParticleSpawner(player));

        switch (this.particle.name()) {
            // Dust Particle.
            case "REDSTONE" -> spawner.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, this.dustOptions);

            // Spawn Dust Transition particles.
            case "DUST_COLOR_TRANSITION" -> spawner.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, this.dustTransition);

            // Spawn in Spell Mob Particles
            case "SPELL_MOB" -> spawner.spawnParticle(this.particle, loc, 0, this.dustColor.getRed() / 255.0, this.dustColor.getGreen() / 255.0, this.dustColor.getBlue() / 255.0, 1.0);
            case "SPELL_MOB_AMBIENT" -> spawner.spawnParticle(this.particle, loc, 0, this.dustColor.getRed() / 255.0, this.dustColor.getGreen() / 255.0, this.dustColor.getBlue() / 255.0, 1.0);

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

    public Color getDustColor() {
        return dustColor;
    }

    public ParticleData setDustColor(Color dustColor) {
        this.dustColor = dustColor;
        return this;
    }

    public Color getTransitionColor() {
        return transitionColor;
    }

    public ParticleData setTransitionColor(Color transitionColor) {
        this.transitionColor = transitionColor;
        return this;
    }

    public int getNote() {
        return this.note;
    }

    public ParticleData setNote(int note) {
        this.note = note;
        return this;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public ParticleData setItemMaterial(Material itemMaterial) {
        this.itemMaterial = itemMaterial;
        return this;
    }

    public Material getBlockMaterial() {
        return blockMaterial;
    }

    public ParticleData setBlockMaterial(Material blockMaterial) {
        this.blockMaterial = blockMaterial;
        return this;
    }

    public ParticleData clone() {
        return new ParticleData(this.particle)
                .setDustColor(this.dustColor)
                .setTransitionColor(this.transitionColor)
                .setNote(this.note)
                .setItemMaterial(this.itemMaterial)
                .setBlockMaterial(this.blockMaterial);
    }

}
