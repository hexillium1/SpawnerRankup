package me.oliver276.spawnerrankup;

import me.oliver276.spawnerrankup.rankup.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main extends JavaPlugin implements Listener{

    private SpawnerRankup spawnerRankup;

    @Override
    public void onEnable() {
        SpawnerRanks.init(this);
        if (setupSpawnerRanks()) {

            Bukkit.getPluginManager().registerEvents(this, this);

            getLogger().info("SpawnerRankup started!");
            getLogger().info("The plugin setup process is complete!");

        } else {

            getLogger().severe("Failed to setup SpawnerRankup!");
            getLogger().severe("Your server version is not compatible with this plugin!");

            Bukkit.getPluginManager().disablePlugin(this);
        }
        saveDefaultConfig();
    }


    // this method will setup our actionbar class and return true if the server is running a
    // version compatible with our NMS classes.
    // If the server is not compatible, it will return false!
    private boolean setupSpawnerRanks() {

        String version;

        try {

            version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];

        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }


        if (version.equals("v1_10_R1")) {
            spawnerRankup = new SpawnerRankup_1_10_R1();
        } else if (version.equals("v1_9_R1")) {
            spawnerRankup = new SpawnerRankup_1_9_R1();
        } else if (version.equals("v1_9_R2")) {
            spawnerRankup = new SpawnerRankup_1_9_R2();
        } else if (version.equals("v1_8_R3")) {
            spawnerRankup = new SpawnerRankup_1_8_R3();
        } else if (version.equals("v1_11_R1")) {
            spawnerRankup = new SpawnerRankup_1_11_R1();
        } else if (version.equals("v1_12_R1")){
            spawnerRankup = new SpawnerRankup_1_12_R1();
        }
        String compatible =  (spawnerRankup == null) ? ChatColor.RED + "is not" : ChatColor.GREEN + "is";
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[SpawnerRankup] " +
                ChatColor.WHITE + "Your server is running version " + version +
                " which " + compatible + ChatColor.WHITE + " with this version of SpawnerRankup.  Please report any " +
                "problems onto the DevBukkit website: https://dev.bukkit.org/projects/spawner-rankup");
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        boolean SS = false;
        for (Plugin plugin : plugins){
            if (plugin.getName().equalsIgnoreCase("SilkSpawners")) SS = true;
        }
        if (SS){
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED +
            "[SpawnerRankup] Warning: you have SilkSpawners on this server, which is known to be incompatible with this plugin."
            + " This plugin will still run, but may not function correctly.");
        }
        return spawnerRankup != null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if (!(e.getInventory().getTitle().contains("Spawner"))) return;
        if (!(e.getInventory().getTitle().startsWith(ChatColor.RED +""))) return;
        if ((e.getInventory().getSize() != 9*5)) return;
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType().equals(Material.AIR)) return;
        if (!e.getWhoClicked().hasPermission("spawnerrankup.gui")) return;
        e.setCancelled(true);
        ItemStack spawnerItem = e.getInventory().getContents()[(9*4)+1-1];
        ItemMeta spawnerItemMeta = spawnerItem.getItemMeta();
        String[] locationStrArray = spawnerItemMeta.getDisplayName().split(",");
        Location location = new Location(Bukkit.getWorld(locationStrArray[3]), Double.valueOf(locationStrArray[0])
                , Double.valueOf(locationStrArray[1])
                , Double.valueOf(locationStrArray[2]));
        Block spawner = Bukkit.getWorld(locationStrArray[3]).getBlockAt(location);
        if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("Spawner is rank")){
            int currentRank = spawnerRankup.determineOverallRank(e.getWhoClicked().getWorld().getBlockAt(location));
            if (currentRank < 1 || currentRank > SpawnerRanks.getInstance().getCompleteUpgradeMax()) currentRank = 1;
            if (currentRank == SpawnerRanks.getInstance().getCompleteUpgradeMax()) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Sorry, the spawner's already at it's highest level.");
            } else {
                if (((Player) e.getWhoClicked()).getLevel() < SpawnerRanks.getInstance().completeUpgradeCost(currentRank)){
                    e.getWhoClicked().sendMessage(ChatColor.RED + "Sorry, you've not got enough XP to level this up. You need "
                            + ChatColor.AQUA + SpawnerRanks.getInstance().completeUpgradeCost(currentRank)
                            + ChatColor.RED + " levels to upgrade it.");
                } else {
                    ((Player) e.getWhoClicked()).setLevel(((Player) e.getWhoClicked()).getLevel() - SpawnerRanks.getInstance().completeUpgradeCost(currentRank));
                    spawnerRankup.spawnerRankUp(currentRank, spawner);
                    addCompleteLevelingItems(e.getInventory(), spawner);
                }
            }
            addInfoItems(e.getClickedInventory(),spawner);
        }else if (e.getCurrentItem().getItemMeta().getDisplayName().startsWith("Break Spawner")){
            BlockBreakEvent spawnerBreak = new BlockBreakEvent(spawner,(Player) e.getWhoClicked());
            Bukkit.getPluginManager().callEvent(spawnerBreak);
            if (spawnerBreak.isCancelled())  return;
            //String entityToSpawn = spawnerRankup.getStringWithinCompound(spawner,"SpawnData","id");
            String entityToSpawn = spawnerRankup.getSpawnerType(spawner);
            ItemStack spawnerItemToDrop = makeItem(Material.MOB_SPAWNER, ChatColor.DARK_GREEN + entityToSpawn + " spawner", Arrays.asList(
                    "SpawnCount: " + spawnerRankup.getShort(spawner,"SpawnCount"),
                    "SpawnRange: " + spawnerRankup.getShort(spawner,"SpawnRange"),
                    "MaxSpawnDelay: " + spawnerRankup.getShort(spawner,"MaxSpawnDelay"),
                    "MinSpawnDelay: " + spawnerRankup.getShort(spawner,"MinSpawnDelay"),
                    "MaxNearbyEntities: " + spawnerRankup.getShort(spawner,"MaxNearbyEntities"),
                    "RequiredPlayerRange: " + spawnerRankup.getShort(spawner,"RequiredPlayerRange")
            ));
            spawner.setType(Material.AIR);
            location.getWorld().dropItemNaturally(location,spawnerItemToDrop);
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e){
        if (!e.getItemInHand().getType().equals(Material.MOB_SPAWNER)) return;
        if (!e.getItemInHand().hasItemMeta()) return;
        if (!e.getItemInHand().getItemMeta().hasDisplayName() || !e.getItemInHand().getItemMeta().hasLore()) return;
        if (e.isCancelled()) return;
        List<String> lore = e.getItemInHand().getItemMeta().getLore();
        String name = e.getItemInHand().getItemMeta().getDisplayName();
        if (!((lore.size()) == 6 && (name.contains(" spawner")))) return;
        for (String tag : lore){
            String[] tags = tag.split(": ");
            spawnerRankup.setShort(e.getBlock(),tags[0].replaceAll(": ",""),Short.valueOf(tags[1].replaceAll(": ","")));
        }
        spawnerRankup.setSpawnerType(e.getBlock(),name.split(" ")[0].replaceAll(" ","").replaceAll(ChatColor.DARK_GREEN + "",""));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
        if (!(e.getClickedBlock().getType().equals(Material.MOB_SPAWNER))) return;
        if (e.getPlayer().isSneaking()) return;
        Block spawner = e.getClickedBlock();
        Inventory inv = Bukkit.createInventory(null,5*9,ChatColor.RED + "" + (spawnerRankup.getSpawnerType(spawner) + " Spawner"));
                /*+ "Spawner @ X:" +
                spawner.getLocation().getBlockX() + ", Y:" +
                spawner.getLocation().getBlockY() + ", Z:" +
                spawner.getLocation().getBlockZ() + ".");*/

        addInfoItems(inv,spawner);

        ItemStack removeBlock = makeItem(Material.BARRIER,"Break Spawner",Collections.singletonList("Break the spawner" +
                " and drop the item"));
        inv.setItem((9*4)+9-1,removeBlock);

        ItemStack mobSpawnerID = makeItem(Material.MOB_SPAWNER,spawner.getLocation().getBlockX() + "," +
                spawner.getLocation().getBlockY() + "," +
                spawner.getLocation().getBlockZ() + "," +
                spawner.getLocation().getWorld().getName()
                , Collections.singletonList("Location of the spawner"));
        inv.setItem((9*4)+1-1,mobSpawnerID);
        SpawnerRanks sr = SpawnerRanks.getInstance();
        if (sr.usingComponents()){

        } else {
            addCompleteLevelingItems(inv,spawner);
        }
        e.setCancelled(true);
        e.getPlayer().openInventory(inv);

        //to do use the PURPLE+ (+NBT) for placing the spawner <- doesn't work with survival mode
        //todo setup GUI with all fields and buttons
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spawnerrankup")){
            if (args.length == 0) {
                sender.sendMessage(ChatColor.YELLOW + "This plugin was developed by oliver276 over at Bukkit forums");
                sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.AQUA + label + " reload" + ChatColor.YELLOW + " to reload the config");
            } else if (args[0].equalsIgnoreCase("reload")){
                if (!sender.hasPermission("spawnerrankup.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this. You need " + ChatColor.AQUA + "spawnerrankup.reload" +
                            ChatColor.RED + " or OP to be able to use this command.");
                }
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "SpawnerRankup config reloaded.");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "This plugin was developed by oliver276 over at Bukkit forums");
                sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.AQUA + label + " reload" + ChatColor.YELLOW + " to reload the config");
            }
        }
        return true;
    }

    private void addUpgradeItems(Inventory inv, Block spawner){

    }

    private void addDowngradeItems(Inventory inv, Block spawner){

    }

    private void addCompleteLevelingItems(Inventory inv, Block spawner){
        SpawnerRanks sr = SpawnerRanks.getInstance();
        int spawnerRank = spawnerRankup.determineOverallRank(spawner);
        ItemStack currentLevelBlock = makeItem(Material.REDSTONE_BLOCK,"Spawner is rank " + spawnerRank,Collections.singletonList(
                "You can upgrade this up to " + ChatColor.AQUA + sr.getCompleteUpgradeMax() +
                        ChatColor.DARK_PURPLE + ChatColor.ITALIC + " times"
        ));
        inv.setItem((1*9)+5-1,currentLevelBlock);
    }

    private void addInfoItems(Inventory inv, Block spawner){
        ItemStack EntityNumberBlock = makeItem(Material.IRON_BLOCK,"SpawnCount", Collections.singletonList(ChatColor.AQUA +
                String.valueOf(spawnerRankup.getShort(spawner, "SpawnCount")) + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " per activation"));
        inv.setItem((9*2)+1-1,EntityNumberBlock);

        ItemStack EntityRangeBlock = makeItem(Material.IRON_BLOCK,"SpawnRange", Collections.singletonList(ChatColor.AQUA +
                String.valueOf(spawnerRankup.getShort(spawner, "SpawnRange")) + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " blocks"));
        inv.setItem((9*2)+3-1,EntityRangeBlock);

        ItemStack SpawnDelayBlock = makeItem(Material.IRON_BLOCK,"SpawnDelay", Arrays.asList("Between " + ChatColor.AQUA +
                        String.valueOf(spawnerRankup.getShort(spawner, "MinSpawnDelay")) + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " and " + ChatColor.AQUA +
                        String.valueOf(spawnerRankup.getShort(spawner, "MaxSpawnDelay")) + " ticks.",
                "Or, " + "Between " + ChatColor.AQUA +
                        String.valueOf((double) spawnerRankup.getShort(spawner, "MinSpawnDelay") / 20d) + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " and " + ChatColor.AQUA +
                        String.valueOf((double) spawnerRankup.getShort(spawner, "MaxSpawnDelay") / 20d) + " seconds."));
        inv.setItem((9*2)+5-1,SpawnDelayBlock);

        ItemStack MaxNearbyBlock = makeItem(Material.IRON_BLOCK,"Maximum Nearby Entity Count", Collections.singletonList("A maximum of " + ChatColor.AQUA +
                String.valueOf(spawnerRankup.getShort(spawner, "MaxNearbyEntities")) + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " entities may be near"));
        inv.setItem((9*2)+7-1,MaxNearbyBlock);

        ItemStack PlayerRangeBlock = makeItem(Material.IRON_BLOCK,"RequiredPlayerRange", Collections.singletonList("A player has to be within " + ChatColor.AQUA +
                String.valueOf(spawnerRankup.getShort(spawner, "RequiredPlayerRange")) + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " blocks to activate this spawner"));
        inv.setItem((9*2)+9-1,PlayerRangeBlock);
    }

    private ItemStack makeItem(Material material, String name, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


}
