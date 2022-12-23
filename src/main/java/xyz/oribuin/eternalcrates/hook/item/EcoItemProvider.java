package xyz.oribuin.eternalcrates.hook.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * @author Esophose via RoseLoot
 */
public class EcoItemProvider implements ItemProvider {

    private Method lookupMethod, getItemMethod;

    public EcoItemProvider() {
        if (!Bukkit.getPluginManager().isPluginEnabled("eco")) {
            return;
        }

        try {
            var itemClass = Class.forName("com.willfp.eco.core.items.Items");
            this.lookupMethod = itemClass.getMethod("lookup", String.class);
            var testableItemClass = Class.forName("com.willfp.eco.core.items.TestableItem");
            this.getItemMethod = testableItemClass.getMethod("getItem");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPluginName() {
        return "eco";
    }

    @Override
    public ItemStack getItem(@NotNull String key, @Nullable Player player) {
        if (this.lookupMethod == null || this.getItemMethod == null) {
            return null;
        }

        try {
            var testableItem = this.lookupMethod.invoke(null, key);
            if (testableItem == null) {
                return null;
            }

            return (ItemStack) this.getItemMethod.invoke(testableItem);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }

    }
}
