package xyz.oribuin.eternalcrates.animation;

import org.bukkit.FireworkEffect;

import java.util.ArrayList;
import java.util.List;

public class CustomFirework {

    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private double fireDelay;
    private double detonationDelay;
    private int power;
    private List<FireworkEffect> effects;

    public CustomFirework() {
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.fireDelay = 0;
        this.detonationDelay = 0;
        this.power = 0;
        this.effects = new ArrayList<>();
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public double getFireDelay() {
        return fireDelay;
    }

    public void setFireDelay(double fireDelay) {
        this.fireDelay = fireDelay;
    }

    public double getDetonationDelay() {
        return detonationDelay;
    }

    public void setDetonationDelay(double detonationDelay) {
        this.detonationDelay = detonationDelay;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public List<FireworkEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<FireworkEffect> effects) {
        this.effects = effects;
    }

}