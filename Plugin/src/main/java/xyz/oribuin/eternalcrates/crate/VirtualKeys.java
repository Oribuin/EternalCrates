package xyz.oribuin.eternalcrates.crate;

import java.util.Map;

public record VirtualKeys(Map<String, Integer> keys) {

    public Map<String, Integer> getKeys() {
        return keys;
    }

}