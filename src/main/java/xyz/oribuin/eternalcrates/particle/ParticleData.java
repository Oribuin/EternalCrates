package xyz.oribuin.eternalcrates.particle;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticleData {

    private final Particle particle;
    private Color dustColor;
    private Color transitionColor;
    private int note;
    private Material itemMaterial;
    private Material blockMaterial;

    public ParticleData(final Particle particle) {
        this.particle = particle;
        this.dustColor = Color.AQUA;
        this.transitionColor = Color.WHITE;
        this.note = 0;
        this.itemMaterial = Material.DIRT;
        this.blockMaterial = Material.DIRT;
    }

    public void spawn(Player player, Location loc, int count) {
        this.spawn(player, loc, count, 0.0, 0.0, 0.0);
    }

    public void spawn(Player player, Location loc, int count, double offsetX, double offsetY, double offsetZ) {
        final var world = loc.getWorld();
        if (world == null)
            return;

        switch (this.particle.name()) {

            // Dust Particle.
            case "REDSTONE" -> {
                Particle.DustOptions options = new Particle.DustOptions(this.dustColor, 1f);
                player.spawnParticle(Particle.REDSTONE, loc, count, offsetX, offsetY, offsetZ, 0, options);
            }

            // Spawn Dust Transition particles.
            case "DUST_COLOR_TRANSITION" -> {
                Particle.DustTransition transition = new Particle.DustTransition(this.dustColor, this.transitionColor, 1f);
                player.spawnParticle(Particle.DUST_COLOR_TRANSITION, loc, count, offsetX, offsetY, offsetZ, 0, transition);
            }

            // Spawn in Spell Mob Particles
            case "SPELL_MOB", "SPELL_MOB_AMBIENT" -> player.spawnParticle(this.particle, loc, 0, this.dustColor.getRed() / 255.0, this.dustColor.getGreen() / 255.0, this.dustColor.getBlue() / 255.0, 1.0);

            // Block Particles
            case "BLOCK_CRACK", "BLOCK_DUST", "FALLING_DUST", "BLOCK_MARKER" -> {
                Material block = this.blockMaterial.isBlock() ? this.blockMaterial : Material.BLACK_WOOL;
                player.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, 0, block.createBlockData());
            }

            // Item Particles
            case "ITEM_CRACK" -> {
                Material item = this.itemMaterial.isItem() ? this.itemMaterial : Material.BLACK_WOOL;
                player.spawnParticle(this.particle, loc, count, offsetX, offsetY, offsetZ, 0, new ItemStack(item));
            }

            // Note Particles
            case "NOTE" -> player.spawnParticle(this.particle, loc, 0, this.note / 24.0, 0, 0, 1);

            case "VIBRATION" -> {
                // fuck vibration particles
            }

            // Any other particle.
            default -> player.spawnParticle(this.particle, loc, count, 0, 0.0, 0.0, 0.0);
        }
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
