package xyz.oribuin.eternalcrates.particle;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;

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
                .filter(noteParticle -> noteParticle.note == note)
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
