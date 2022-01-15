package xyz.oribuin.eternalcrates.crate;

import java.util.Map;
import java.util.UUID;

public class VirtualKeys {

    private final UUID uuid;
    private Map<String, Integer> keys;

    public VirtualKeys(final UUID uuid, Map<String, Integer> keys) {
        this.uuid = uuid;
        this.keys = keys;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, Integer> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Integer> keys) {
        this.keys = keys;
    }

}
