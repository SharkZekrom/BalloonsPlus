package be.shark_zekrom.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Skulls {

    public static ItemStack createSkull(String url) throws MalformedURLException {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);

        if(itemStack.getItemMeta() instanceof SkullMeta skullMeta){
            var playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
            playerProfile.getTextures().setSkin(URI.create(url).toURL());
            skullMeta.setOwnerProfile(playerProfile);
            itemStack.setItemMeta(skullMeta);
        }
        return itemStack;
    }


    public static String getSkull(ItemStack head) {
        if (head == null || head.getType() != Material.PLAYER_HEAD) return null;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        if (headMeta == null) return null;

        try {
            // Récupère le champ "profile" de la SkullMeta
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);

            GameProfile profile = (GameProfile) profileField.get(headMeta);
            if (profile == null) return null;

            // Retourne l'URL de la texture
            Property textureProperty = profile.getProperties().get("textures").stream().findFirst().orElse(null);
            return textureProperty != null ? textureProperty.getValue() : null;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }



}
