package xyz.oribuin.eternalcrates.crate;

import java.util.HashMap;
import java.util.Map;

public class CrateKeys {

    private Map<String, Integer> keys;

    public CrateKeys(Map<String, Integer> keys) {
        this.keys = keys;
    }

    public CrateKeys() {
        this.keys = new HashMap<>();
    }

    public Map<String, Integer> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, Integer> keys) {
        this.keys = keys;
    }

}
