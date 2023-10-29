package xyz.oribuin.eternalcrates.util.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.rosewood.rosegarden.utils.NMSUtil;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public final class SkullUtils {

    private static Field field_SkullMeta_profile;

    private SkullUtils() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    /**
     * Applies a base64 encoded texture to an item's SkullMeta
     *
     * @param skullMeta The ItemMeta for the Skull
     * @param texture The texture to apply to the skull
     */
    public static void setSkullTexture(SkullMeta skullMeta, String texture) {
        if (texture == null || texture.isEmpty())
            return;

        if (texture.startsWith("hdb:") && Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            texture = new HeadDatabaseAPI().getBase64(texture.substring(4));
            if (texture == null)
                return;
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), "texture");
        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            if (field_SkullMeta_profile == null) {
                field_SkullMeta_profile = skullMeta.getClass().getDeclaredField("profile");
                field_SkullMeta_profile.setAccessible(true);
            }

            field_SkullMeta_profile.set(skullMeta, profile);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

    }

}