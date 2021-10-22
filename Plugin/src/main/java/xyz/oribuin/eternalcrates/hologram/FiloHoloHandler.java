package xyz.oribuin.eternalcrates.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FiloHoloHandler implements HologramHandler {

    private final Map<Location, Hologram> holograms = new HashMap<>();

    @Override
    public void createOrUpdateHologram(Location location, List<String> text) {
        Hologram hologram = this.holograms.get(location);

    }

    @Override
    public void deleteHologram(Location location) {

    }

    @Override
    public void deleteAllHolograms() {

    }

    @Override
    public boolean isHologram(Entity entity) {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
