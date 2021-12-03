package xyz.oribuin.eternalcrates.particle;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ParticleData {

    private final Particle particle;
    private Color dustColor;
    private Color transitionColor;
    private NoteParticle note;

    private Material itemMaterial;
    private Material blockMaterial;

    public ParticleData(final Particle particle) {
        this.particle = particle;
        this.dustColor = Color.AQUA;
        this.transitionColor = Color.WHITE;
        this.note = NoteParticle.CRIMSON;
        this.itemMaterial = Material.DIRT;
        this.blockMaterial = Material.DIRT;
    }

    public void spawn(Player player, Location loc, int count) {
        this.spawn(player, loc, count, 0.0, 0.0, 0.0);
    }

    public void spawn(Player player, Location loc, int count, double offsetX, double offsetY, double offsetZ) {
        final World world = loc.getWorld();
        if (world == null)
            return;

        switch (this.getParticle().name()) {

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
            case "NOTE" -> player.spawnParticle(this.particle, loc, 0, this.note.getNoteNumber() / 24.0, 0, 0, 1);


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

    public void setDustColor(Color dustColor) {
        this.dustColor = dustColor;
    }

    public Color getTransitionColor() {
        return transitionColor;
    }

    public void setTransitionColor(Color transitionColor) {
        this.transitionColor = transitionColor;
    }

    public NoteParticle getNote() {
        return note;
    }

    public void setNote(NoteParticle note) {
        this.note = note;
    }

    public void setNote(int note) {
        this.note = Arrays.stream(NoteParticle.values())
                .filter(noteParticle -> noteParticle.getNoteNumber() == note)
                .findAny()
                .orElse(NoteParticle.CRIMSON);
    }


    public Material getItemMaterial() {
        return itemMaterial;
    }

    public void setItemMaterial(Material itemMaterial) {
        this.itemMaterial = itemMaterial;
    }

    public Material getBlockMaterial() {
        return blockMaterial;
    }

    public void setBlockMaterial(Material blockMaterial) {
        this.blockMaterial = blockMaterial;
    }

    public ParticleData clone() {
        ParticleData data = null;

        try {
            data = (ParticleData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return data;
    }
}
