package xyz.oribuin.eternalcrates.crate;

import java.util.HashMap;
import java.util.Map;

public class CrateKeys {

    private Map<String, Integer> content;

    /**
     * Create a new CrateKeys object with pre-existing data
     *
     * @param content The keys
     */
    public CrateKeys(Map<String, Integer> content) {
        this.content = content;
    }

    /**
     * Create an empty CrateKeys object
     */
    public CrateKeys() {
        this(new HashMap<>());
    }

    /**
     * Get the amount of keys a player has for a crate
     *
     * @param crateId The crate id
     */
    public int get(String crateId) {
        return this.content.getOrDefault(crateId, 0);
    }

    /**
     * Set the amount of keys a player has for a crate
     *
     * @param crateId The crate id
     * @param amount  The amount of keys
     */
    public void set(String crateId, int amount) {
        this.content.put(crateId, amount);
    }

    /**
     * Modify the whole map of keys
     *
     * @param keys The new keys
     */
    public void set(Map<String, Integer> keys) {
        this.content = keys;
    }

    /**
     * Modify the whole map of keys
     *
     * @param keys The new keys
     */
    public void set(CrateKeys keys) {
        this.content = keys.content;
    }

    /**
     * Add keys to a player
     *
     * @param crateId The crate id
     * @param amount  The amount of keys
     */
    public void add(String crateId, int amount) {
        this.content.put(crateId, this.get(crateId) + amount);
    }

    /**
     * Remove keys from a player
     *
     * @param crateId The crate id
     * @param amount  The amount of keys
     */
    public void remove(String crateId, int amount) {
        this.content.put(crateId, this.get(crateId) - amount);
    }

    /**
     * Check if a player has keys for a crate
     *
     * @param crateId The crate id
     * @param amount  The amount of keys
     * @return If the player has keys
     */
    public boolean has(String crateId, int amount) {
        return this.get(crateId) >= amount;
    }

    /**
     * Get the whole map of keys
     *
     * @return The map of keys
     */
    public Map<String, Integer> getContent() {
        return content;
    }

}
