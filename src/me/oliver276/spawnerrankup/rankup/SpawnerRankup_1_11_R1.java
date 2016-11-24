package me.oliver276.spawnerrankup.rankup;

import me.oliver276.spawnerrankup.SpawnerRanks;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.TileEntity;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftCreatureSpawner;

import java.util.HashMap;

public class SpawnerRankup_1_11_R1 implements SpawnerRankup{

    private final short SpawnCount = 4;
    private final short SpawnRange = 4;
    private final short MaxSpawnDelay = 800;
    private final short MinSpawnDelay = 200;
    private final short MaxNearbyEntityCount = 6;
    private final short RequiredPlayerRange = 16;

    public NBTTagCompound getCompound(Block block, String key){
        TileEntity s = getTileEntity(block);
        NBTTagCompound sNBT = s.d();
        return sNBT.getCompound(key);
    }

    public void setSpawnerType(Block spawner,String entityType){
        CreatureSpawner ms = (CreatureSpawner) spawner;
        ms.setCreatureTypeByName(entityType);
    }

    public int determineOverallRank(Block block){
        TileEntity s = getTileEntity(block);
        NBTTagCompound sNBT = s.d();
        HashMap<String,Short> upgrades = SpawnerRanks.getInstance().getUpgradesComplete();
        short[] shorts = new short[6];
        shorts[0] = (short) (sNBT.getInt("SpawnCount") - SpawnCount);
        shorts[1] = (short) (sNBT.getInt("SpawnRange") - SpawnRange);
        shorts[2] = (short) (sNBT.getInt("MaxSpawnDelay") - MaxSpawnDelay);
        shorts[3] = (short) (sNBT.getInt("MinSpawnDelay") - MinSpawnDelay);
        shorts[4] = (short) (sNBT.getInt("MaxNearbyEntities") - MaxNearbyEntityCount);
        shorts[5] = (short) (sNBT.getInt("RequiredPlayerRange") - RequiredPlayerRange);

        int tempTotal = 0;
        int nonZeroes = 0;
        String[] strings = new String[]{"SpawnCount","SpawnRange","MaxSpawnDelay","MinSpawnDelay","MaxNearbyEntities","RequiredPlayerRange"};

        for(int x = 0; x < 6; x++){
            if (upgrades.get(strings[x]) != 0) {
                tempTotal += shorts[x] / upgrades.get(strings[x]);
                nonZeroes ++;
            }
        }

        return Math.round(tempTotal / nonZeroes) + 1;

    }

    @Override
    public String getStringWithinCompound(Block block, String compoundKey, String stringKey) {
        TileEntity s = getTileEntity(block);
        NBTTagCompound sNBT = s.d();
        return sNBT.getCompound(compoundKey).getString(stringKey);
    }

    @Override
    public void setStringWithinCompound(Block block, String compoundKey, String stringKey, String stringValue) {
        TileEntity s = getTileEntity(block);
        NBTTagCompound sNBT = s.d();
        NBTTagCompound NBTc = new NBTTagCompound();
        NBTc.setString(stringKey,stringValue);
        sNBT.set(compoundKey, (NBTc));
    }

    public short getShort(Block block, String key){
        TileEntity s = getTileEntity(block);
        NBTTagCompound sNBT = s.d();
        return sNBT.getShort(key);
    }

    @Override
    public void spawnerRankUp(int currentRank, Block spawner) {
        SpawnerRanks srs = SpawnerRanks.getInstance();
        HashMap<String,Short> values = srs.getUpgradesComplete();
        TileEntity s = getTileEntity(spawner);
        NBTTagCompound sNBT = s.d();
        sNBT.setShort("SpawnCount",(short) (sNBT.getShort("SpawnCount") + values.get("SpawnCount")));
        sNBT.setShort("SpawnRange",(short) (sNBT.getShort("SpawnRange") + values.get("SpawnRange")));
        sNBT.setShort("Delay",(short) 0);
        sNBT.setShort("MaxSpawnDelay",(short) (sNBT.getShort("MaxSpawnDelay") + values.get("MaxSpawnDelay")));
        sNBT.setShort("MinSpawnDelay", (short) (sNBT.getShort("MinSpawnDelay") + values.get("MinSpawnDelay")));
        sNBT.setShort("MaxNearbyEntities",(short) (sNBT.getShort("MaxNearbyEntities") + values.get("MaxNearbyEntities")));
        sNBT.setShort("RequiredPlayerRange",(short) (sNBT.getShort("RequiredPlayerRange") + values.get("RequiredPlayerRange")));
        sNBT.setShort("SpawnerRank",(short) (currentRank + 1));
        s.a(sNBT);

    }

    public String getSpawnerType(Block spawner){
        CraftCreatureSpawner cs = (CraftCreatureSpawner) spawner;
        return cs.getCreatureTypeName();
    }

    public int getInt(Block block, String key){
        TileEntity s = getTileEntity(block);
        NBTTagCompound sNBT = s.d();
        return sNBT.getInt(key);

    }


    public boolean exists(Block block, String key){
        TileEntity s = getTileEntity(block);
        NBTTagCompound sNBT = s.d();
        return sNBT.hasKey(key);
    }

    public TileEntity getTileEntity(Block tileEntityBlock){
        return ((CraftWorld) tileEntityBlock.getWorld()).getHandle().getTileEntity(new BlockPosition(
                tileEntityBlock.getLocation().getX(),
                tileEntityBlock.getLocation().getY(),
                tileEntityBlock.getLocation().getZ()));
    }

    public void setShort(Block tileEntityBlock, String key, short value){
        TileEntity s = getTileEntity(tileEntityBlock);
        NBTTagCompound sNBT = s.d();
        sNBT.setShort(key,value);
        s.a(sNBT);
        s.save(sNBT);
    }

    public void setString(Block tileEntityBlock, String key, String value){
        TileEntity s = getTileEntity(tileEntityBlock);
        NBTTagCompound sNBT = s.d();
        sNBT.setString(key,value);
        s.a(sNBT);
        s.save(sNBT);
    }

    @Override
    public String getString(Block block, String key) {
        TileEntity s = getTileEntity(block);
        NBTTagCompound sNBT = s.d();
        return sNBT.getString(key);
    }

}
