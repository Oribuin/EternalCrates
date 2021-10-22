package xyz.oribuin.eternalcrates.hologram;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CMIHoloHandler implements HologramHandler {

    private final HologramManager manager = CMI.getInstance().getHologramManager();
    private final Map<Location, CMIHologram> holograms = new HashMap<>();

    @Override
    public void createOrUpdateHologram(Location location, List<String> text) {
        CMIHologram hologram = this.holograms.get(location);

        if (hologram == null) {
            hologram = new CMIHologram(PluginUtils.locationAsKey(location), new CMILocation(location.clone().add(0.0, 1.0, 0.0)));
            hologram.setLines(text);
            manager.addHologram(hologram);
            this.holograms.put(location, hologram);
        } else {
            hologram.setLines(text);
        }

        hologram.update();
    }

    @Override
    public void deleteHologram(Location location) {
        CMIHologram hologram = this.holograms.get(location);
        if (hologram != null) {
            manager.removeHolo(hologram);
            holograms.remove(location);
        }
    }

    @Override
    public void deleteAllHolograms() {
        new HashSet<>(holograms.keySet()).forEach(this::deleteHologram);

    }

    @Override
    public boolean isHologram(Entity entity) {
        return false; // CMI Holograms use packets, not entities.
    }

    @Override
    public boolean isEnabled() {
        return CMIModule.holograms.isEnabled();
    }
}
