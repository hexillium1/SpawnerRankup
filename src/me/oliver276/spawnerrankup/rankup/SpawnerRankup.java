package me.oliver276.spawnerrankup.rankup;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;

public interface SpawnerRankup {

    public void spawnerRankUp(int currentRank, Block spawner);
    public short getShort(Block block, String key);
    public void setString(Block block, String key, String value);
    public String getString(Block block, String key);
    public int getInt(Block block, String key);
    public boolean exists(Block block,String key);
    public void setShort(Block tileEntityBlock, String key, short value);
    public int determineOverallRank(Block block);
    public String getStringWithinCompound(Block block, String compoundKey, String stringKey);
    public void setStringWithinCompound(Block block, String compoundKey, String stringKey, String stringValue);
    public void setSpawnerType(Block spawner,String entityType);

}
