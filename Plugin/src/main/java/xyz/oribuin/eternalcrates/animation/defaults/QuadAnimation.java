package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.crate.Crate;
import xyz.oribuin.eternalcrates.util.MathL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuadAnimation extends ParticleAnimation {

    private int step = 0;
    private double height = 0;
    private final int numSteps = 120;
    private double radius = 1.0;

    public QuadAnimation() {
        super("Quad", "Oribuin", 1);
    }

    @Override
    public List<Location> particleLocations(Location crate) {
        final List<Location> locs = new ArrayList<>();
        int orbs = 4;
        final Location newLoc = crate.clone().subtract(0.0, 0.5, 0.0);
        for (int i = 0; i < orbs; i++) {
            double dx = -(MathL.cos((this.step / (double) this.numSteps) * (Math.PI * 2) + (((Math.PI * 2) / orbs) * i))) * this.radius;
            double dz = -(MathL.sin((this.step / (double) this.numSteps) * (Math.PI * 2) + (((Math.PI * 2) / orbs) * i))) * this.radius;
            locs.add(newLoc.clone().add(dx, height, dz));
        }

        return locs;
    }

    @Override
    public void updateTimer() {
        this.step = (this.step + 3) % this.numSteps;
        this.radius += 0.02;
        this.height += 0.02;
    }

    @Override
    public Map<String, Object> getRequiredValues() {
        final Map<String, Object> options = new HashMap<>();
        options.put("animation.particle", "REDSTONE");
        options.put("animation.color", "#FFFFFF");
        options.put("animation.transition", "#ff0000");
        options.put("animation.note", 1);
        options.put("animation.item", "STONE");
        options.put("animation.block", "STONE");
        return options;
    }

    public void load() {
        // Nothing to load
    }

    @Override
    public void finishFunction(Player player, Crate crate) {
        this.radius = 1.0;
        this.height = 0.0;

        for (int i = 0; i < 15; i++)
            this.getParticleData().spawn(player, crate.getLocation().clone(), 2, 1.0, 1.0, 1.0);
    }

}
