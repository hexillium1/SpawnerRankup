package me.oliver276.spawnerrankup.rankup;

import me.oliver276.spawnerrankup.SpawnerRanks;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;

import java.util.HashMap;

public class SpawnerRankup_1_8_R3 implements SpawnerRankup{

    private final short SpawnCount = 4;
    private final short SpawnRange = 4;
    private final short MaxSpawnDelay = 800;
    private final short MinSpawnDelay = 200;
    private final short MaxNearbyEntityCount = 6;
    private final short RequiredPlayerRange = 16;

    public NBTTagCompound getCompound(Block block, String key){
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(block);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        return sNBT.getCompound(key);
    }

    public void setSpawnerType(Block spawner,String entityType){
        TileEntity s = getTileEntity(spawner);
        TileEntityMobSpawner ms = (TileEntityMobSpawner) s;
        ms.getSpawner().setMobName(entityType);
    }

    public int determineOverallRank(Block block){
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(block);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
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
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(block);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        return sNBT.getCompound(compoundKey).getString(stringKey);
    }

    @Override
    public void setStringWithinCompound(Block block, String compoundKey, String stringKey, String stringValue) {
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(block);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        NBTTagCompound NBTc = new NBTTagCompound();
        NBTc.setString(stringKey,stringValue);
        sNBT.set(compoundKey, (NBTc));
        s.a(sNBT);

    }

    public short getShort(Block block, String key){
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(block);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        return sNBT.getShort(key);
    }

    @Override
    public void spawnerRankUp(int currentRank, Block spawner) {
        SpawnerRanks srs = SpawnerRanks.getInstance();
        HashMap<String,Short> values = srs.getUpgradesComplete();
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(spawner);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
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
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(block);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        return sNBT.getInt(key);

    }


    public boolean exists(Block block, String key){
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(block);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        return sNBT.hasKey(key);
    }

    public TileEntity getTileEntity(Block tileEntityBlock){
        return ((CraftWorld) tileEntityBlock.getWorld()).getHandle().getTileEntity(new BlockPosition(
                tileEntityBlock.getLocation().getX(),
                tileEntityBlock.getLocation().getY(),
                tileEntityBlock.getLocation().getZ()));
    }

    public void setShort(Block tileEntityBlock, String key, short value){
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(tileEntityBlock);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        sNBT.setShort(key, value);
        s.a(sNBT);
    }

    public void setString(Block tileEntityBlock, String key, String value){
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(tileEntityBlock);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        sNBT.setString(key, value);
        s.a(sNBT);
    }

    @Override
    public String getString(Block block, String key) {
        TileEntityMobSpawner s = (TileEntityMobSpawner) getTileEntity(block);
        NBTTagCompound sNBT = new NBTTagCompound();
        s.b(sNBT);
        return sNBT.getString(key);
    }

}
