package be.shark_zekrom;

import be.shark_zekrom.utils.Skulls;
import be.shark_zekrom.utils.SummonBalloons;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.net.MalformedURLException;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Parrot parrot) {
            if (SummonBalloons.balloons.containsValue(parrot)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (SummonBalloons.balloons.containsKey(player)) {
            ArmorStand as = SummonBalloons.as.get(player);

            ItemStack item = as.getEquipment().getHelmet();

            SummonBalloons.as.remove(player);
            as.remove();

            Parrot parrot = SummonBalloons.balloons.get(player);
            SummonBalloons.balloons.remove(player);
            parrot.remove();

            new BukkitRunnable() {
                @Override
                public void run() {
                    SummonBalloons.summonBalloon(player, item, SummonBalloons.percentage.get(player));

                }
            }.runTaskLater(Main.getInstance(), 10L);

        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (SummonBalloons.balloons.containsKey(player)) {
            if (Main.BalloonWithItemInInventory) {
                SummonBalloons.removeBalloonWithGiveItem(player);
            } else {
                SummonBalloons.removeBalloon(player);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (SummonBalloons.balloons.containsKey(player)) {
            if (Main.BalloonWithItemInInventory) {
                SummonBalloons.removeBalloonWithGiveItem(player);
            } else {
                SummonBalloons.removeBalloon(player);
            }
        }
    }

    @EventHandler
    public void onLeash(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        if (SummonBalloons.balloons.containsKey(player)) {
            event.setCancelled(true);
            for (Entity entity : player.getWorld().getNearbyEntities(player.getTargetBlock(null, 50).getLocation(), 0.5, 0.5, 0.5)) {
                if (entity instanceof LeashHitch) {
                    entity.remove();

                }
            }
        }
    }

    @EventHandler
    public void onUnLeash(PlayerUnleashEntityEvent event) {
        if (event.getEntity() instanceof Parrot parrot) {
            if (SummonBalloons.balloons.containsValue(parrot)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity instanceof ArmorStand as) {
            if (SummonBalloons.as.containsValue(as)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (SummonBalloons.balloons.containsKey(player)) {
                if (Main.BalloonWithItemInInventory) {
                    SummonBalloons.removeBalloonWithGiveItem(player);
                } else {
                    SummonBalloons.removeBalloon(player);
                }

            }
        }
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) throws MalformedURLException {
        if (event.getEntity() instanceof Player player) {


            if (SummonBalloons.playerBalloons.containsKey(player)) {
                if (!Main.BalloonWithItemInInventory) {

                    if (SummonBalloons.as.get(player) == null) {


                        if (Main.getInstance().getConfig().getString("Balloons." + SummonBalloons.playerBalloons.get(player) + ".item") != null) {

                            ItemStack itemStack = new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("Balloons." + SummonBalloons.playerBalloons.get(player) + ".item")));
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setCustomModelData(Main.getInstance().getConfig().getInt("Balloons." + SummonBalloons.playerBalloons.get(player) + ".custommodeldata"));
                            itemStack.setItemMeta(itemMeta);

                            SummonBalloons.summonBalloon(player, itemStack, 100.0);
                            SummonBalloons.as.get(player).getEquipment().setHelmet(itemStack);
                        } else {

                            SummonBalloons.summonBalloon(player, Skulls.createSkull(Main.getInstance().getConfig().getString("Balloons." + SummonBalloons.playerBalloons.get(player) + ".head")), 100.0);

                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Parrot parrot) {
            if (SummonBalloons.balloons.containsValue(parrot)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if (!Main.BalloonWithItemInInventory) return;

        if (!player.isInsideVehicle()) {

            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                if (item != null) {
                    if (item.hasItemMeta()) {
                        if (item.getItemMeta().hasDisplayName()) {
                            if (item.getItemMeta().getDisplayName().contains("§eBalloons+ : ")) {
                                event.setCancelled(true);
                                if (!SummonBalloons.balloons.containsKey(player)) {

                                    SummonBalloons.playerBalloons.put(player, item.getItemMeta().getDisplayName().split(" : ")[1]);
                                    String percentageBalloon = item.getItemMeta().getLore().get(0).split(" : ")[1].replace("%", "");

                                    if (Double.parseDouble(percentageBalloon) > 0.0) {
                                        SummonBalloons.summonBalloon(player, player.getEquipment().getItemInMainHand(), Double.parseDouble(percentageBalloon));
                                        player.getEquipment().setItem(EquipmentSlot.HAND, null);

                                    }
                                }

                            }
                        }
                    }
                }

            }
        }
    }


}

