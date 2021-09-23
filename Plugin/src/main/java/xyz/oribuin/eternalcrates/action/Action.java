package xyz.oribuin.eternalcrates.action;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalcrates.EternalCrates;

public abstract class Action {

    private String message = "";

    public abstract String actionType();

    public abstract void executeAction(EternalCrates plugin, Player player);

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
