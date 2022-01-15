package xyz.oribuin.eternalcrates.crate;

import java.util.Map;

public class VirtualKeys {

    private final Map<String, Integer> keys;

    public VirtualKeys(final Map<String, Integer> keys) {
        this.keys = keys;
    }

    public Map<String, Integer> getKeys() {
        return keys;
    }

}
