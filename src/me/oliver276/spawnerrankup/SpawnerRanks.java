package me.oliver276.spawnerrankup;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class SpawnerRanks {
    private static SpawnerRanks ourInstance;
    private static Plugin plugin;

    public static SpawnerRanks getInstance() {
        return ourInstance;
    }

    public static void init(Plugin plugin){
        ourInstance = new SpawnerRanks();
        SpawnerRanks.plugin = plugin;
    }

    private SpawnerRanks() {

    }

    public double getComponentUpgradeCost(String component){
        return plugin.getConfig().getDouble("components." + component + ".LevelupCost");
    }
    public short getComponentUpgradeValue(String component){
        return (short) plugin.getConfig().getInt("components." + component + ".LevelupValue");
    }
    public boolean canUpgradeComponent(String component,int currentRank){
        return currentRank < plugin.getConfig().getInt("components." + component + ".MaxUpgrades");
    }

    public int completeUpgradeCost(int currentRank){
        return (plugin.getConfig().getIntegerList("levelups.Costs").get(currentRank));
        //as currentrank +1 for the next and -1 for the index = +- 0
    }
    public boolean canUpgradeComplete(int currentRank){
        return currentRank < plugin.getConfig().getDoubleList("levelups.Costs").size();
    }
    public int getCompleteUpgradeMax(){
        return plugin.getConfig().getDoubleList("levelups.Costs").size();
    }
    public HashMap<String,Short> getUpgradesComplete(){
        HashMap<String,Short> values = new HashMap<>();
        values.put("SpawnCount",(short) plugin.getConfig().getInt("levelups.SpawnCount"));
        values.put("SpawnRange",(short) plugin.getConfig().getInt("levelups.SpawnRange"));
        values.put("MaxSpawnDelay",(short) plugin.getConfig().getInt("levelups.MaxSpawnDelay"));
        values.put("MinSpawnDelay",(short) plugin.getConfig().getInt("levelups.MinSpawnDelay"));
        values.put("MaxNearbyEntities",(short) plugin.getConfig().getInt("levelups.MaxNearbyEntities"));
        values.put("RequiredPlayerRange",(short) plugin.getConfig().getInt("levelups.RequiredPlayerRange"));
        return values;
    }

    public boolean usingComponents(){
        return !plugin.getConfig().getBoolean("levelup-whole-spawner");
    }

    public boolean usingWholeLevelup(){
        return plugin.getConfig().getBoolean("levelup-whole-spawner");
    }
}
