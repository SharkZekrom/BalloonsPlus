package be.shark_zekrom;

import be.shark_zekrom.utils.Skulls;
import be.shark_zekrom.utils.SummonBalloons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String string, String[] args) {

        Player player = (Player) commandSender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage("§b==========[Balloons+]==========");
                player.sendMessage(String.valueOf(ChatColor.AQUA));
                player.sendMessage(ChatColor.AQUA + "/balloons+ help");
                player.sendMessage(ChatColor.AQUA + "/balloons+ reload");
                player.sendMessage(ChatColor.AQUA + "/balloons+ inventory");
                player.sendMessage(ChatColor.AQUA + "/balloons+ spawn <name>");
                player.sendMessage(ChatColor.AQUA + "/balloons+ create <name>");
                player.sendMessage(ChatColor.AQUA + "/balloons+ set <player> <balloon>");
                player.sendMessage(ChatColor.AQUA + "/balloons+ remove");
                player.sendMessage(ChatColor.AQUA + "/balloons+ recipes create");
                player.sendMessage(ChatColor.AQUA + "/balloons+ recipes list");
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (SummonBalloons.balloons.containsKey(player)) {
                    SummonBalloons.removeBalloonWithGiveItem(player);
                    SummonBalloons.playerBalloons.remove(player);

                    player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("BalloonsRemoved"));

                }
            }
            if (args[0].equalsIgnoreCase("inventory")) {
                if (Main.BalloonWithItemInInventory) {
                    player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("CantOpenInventoryWithBalloonWithItemInInventory"));
                } else {
                    if (player.isInsideVehicle()) {
                        player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("CantOpenInventory"));

                    } else {
                        try {
                            Menu.inventory(player, 0);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("balloons+.reload")) {
                    reload();
                    player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("BalloonReload"));
                } else {
                    player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("NoPermission"));
                }


            }


        } else if (args.length > 1) {
            if (args[0].equalsIgnoreCase("recipes")) {
                if (args[1].equalsIgnoreCase("create")) {
                    if (player.hasPermission("balloons+.recipes.create")) {
                        RecipesGui.recipeCreateGui(player);
                    }
                }
                if (args[1].equalsIgnoreCase("list")) {
                    if (Main.BalloonWithItemInInventory) {
                        RecipesGui.recipeListGui(player, 0);
                    }
                }
                if (args[1].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("balloons+.recipes.reload")) {
                        Recipes.loadRecipes();
                    }
                }
            }
            if (args[0].equalsIgnoreCase("spawn")) {
                if (Main.BalloonWithItemInInventory) {
                    player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("CantOpenInventoryWithBalloonWithItemInInventory"));
                } else {
                    File file = new File(Main.getInstance().getDataFolder(), "config.yml");
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    if (config.getString("Balloons." + args[1]) != null) {

                        String permission = config.getString("Balloons." + args[1] + ".permission");
                        if (commandSender.hasPermission(permission)) {
                            if (SummonBalloons.balloons.containsKey(player)) {
                                if (config.getString("Balloons." + args[1] + ".item") != null) {
                                    ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("Balloons." + args[1] + ".item")));
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    itemMeta.setCustomModelData(config.getInt("Balloons." + args[1] + ".custommodeldata"));
                                    itemStack.setItemMeta(itemMeta);
                                    SummonBalloons.as.get(player).getEquipment().setHelmet(itemStack);
                                } else {
                                    try {
                                        SummonBalloons.as.get(player).getEquipment().setHelmet(Skulls.createSkull(config.getString("Balloons." + args[1] + ".head")));
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            } else {
                                if (config.getString("Balloons." + args[1] + ".item") != null) {
                                    ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("Balloons." + args[1] + ".item")));
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    itemMeta.setCustomModelData(config.getInt("Balloons." + args[1] + ".custommodeldata"));
                                    itemStack.setItemMeta(itemMeta);
                                    SummonBalloons.summonBalloon(player, itemStack, 100.0);


                                } else {
                                    try {
                                        SummonBalloons.summonBalloon(player, Skulls.createSkull(config.getString("Balloons." + args[1] + ".head")), 100.0);
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }

                                }

                            }
                            SummonBalloons.playerBalloons.put(player, args[1]);
                            player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("BalloonsSummoned"));

                        } else {
                            player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("NoBalloonsPermission"));
                        }
                    } else {
                        player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("NoBalloonsFound"));
                    }
                }
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (player.hasPermission("balloons+.create")) {
                    Menu.playerIdCreate.put(player, args[1]);
                    Menu.createInventory(player);
                }
            }

            /*
            if (args[0].equalsIgnoreCase("unequip")) {
                if (player.hasPermission("balloons+.remove")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        SummonBalloons.removeBalloon(target);
                        player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("BalloonRemovedForPlayer"));
                    }
                }
            }
            if (args[0].equalsIgnoreCase("set")) {
                if (player.hasPermission("balloons+.set")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        //SUMMON BALLOON
                        player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("BalloonSetForPlayer"));
                    }
                }
            }

             */

        } else {
            if (Main.BalloonWithItemInInventory) {
                player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("CantOpenInventoryWithBalloonWithItemInInventory"));
            } else {
                if (player.isInsideVehicle()) {
                    player.sendMessage(Main.prefix + Main.getInstance().getConfig().getString("CantOpenInventory"));

                } else {
                    try {
                        Menu.inventory(player, 0);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
        return false;
    }


    public static void reload() {
        try {
            Main.getInstance().getConfig().load(new File(Main.getInstance().getDataFolder(), "config.yml"));

            Menu.list.clear();
            ConfigurationSection cs = Main.getInstance().getConfig().getConfigurationSection("Balloons");
            Menu.list.addAll(cs.getKeys(false));

            Main.showOnlyBallonsWithPermission = Main.getInstance().getConfig().getBoolean("ShowOnlyBalloonsWithPermission");
            Main.prefix = Main.getInstance().getConfig().getString("BalloonPrefix");


        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            arguments.add("help");
            arguments.add("inventory");
            arguments.add("spawn");
            arguments.add("remove");
            arguments.add("set");
            arguments.add("recipes");

            if (sender.hasPermission("balloon+.*")) {
                arguments.add("reload");
                arguments.add("create");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("recipes")) {
            if (sender.hasPermission("balloon+.*")) {
                arguments.add("create");

            }
            arguments.add("list");

        }

        return arguments;
    }

}