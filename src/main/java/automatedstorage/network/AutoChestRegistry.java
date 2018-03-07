/* Automated Chests Minecraft Mod
 * Copyright (C) 2018 Diego Darriba
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package automatedstorage.network;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import automatedstorage.AutomatedStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class AutoChestRegistry extends WorldSavedData
{

  private static final String DATA_NAME = AutomatedStorage.modId + "_GlobalAutoChests";
  private static final String TAG_LIST_NAME = "GlobalAutoChests";

  private final Map<Integer, Set<BlockPos>> autoChestRegistry = Maps.newHashMap();
  private final Map<BlockPos, Integer> reverseLookup = Maps.newHashMap();

  public AutoChestRegistry()
  {
    super(DATA_NAME);
  }

  public AutoChestRegistry(String name)
  {
    super(name);
  }

  @Nullable
  public Integer getNetworkFor(BlockPos pos)
  {
    return reverseLookup.get(pos);
  }
  
  public void addAutoChest(int networkId, BlockPos pos, int type)
  {
    if (reverseLookup.containsKey(pos))
    {
      /* remove old connection */
      autoChestRegistry.get(getNetworkFor(pos)).remove(pos);
    }

    if (!autoChestRegistry.containsKey(networkId))
    {
      autoChestRegistry.put(networkId, new LinkedHashSet<BlockPos>());
    }
    
    //TODO Insert at beginning if type = 0, at the end otherwise
    if (type == 0)
      autoChestRegistry.get(networkId).add(pos);
    else
      autoChestRegistry.get(networkId).add(pos);
    
    /* set/update reverse lookup */
    reverseLookup.put(pos, networkId);
    
    markDirty();
  }
  
  public void removeAutoChest(BlockPos pos)
  {
    if (reverseLookup.containsKey(pos))
    {
      autoChestRegistry.get(getNetworkFor(pos)).remove(pos);
      reverseLookup.remove(pos);
    }
    markDirty();
  }

  public Collection<Set<BlockPos>> getAutoChests()
  {
    return autoChestRegistry.values();
  }

  public Set<BlockPos> getAutoChests(int networkId)
  {
    if (!autoChestRegistry.containsKey(networkId))
      autoChestRegistry.put(networkId, new LinkedHashSet<BlockPos>());
    return autoChestRegistry.get(networkId);
  }

  public Set<Integer> getNetworks()
  {
    return autoChestRegistry.keySet();
  }
  
  public Set<BlockPos> getLookupKeys()
  {
    return reverseLookup.keySet();
  }
  
  @Override
  public void readFromNBT(NBTTagCompound tagCompound)
  {
    int currentKey = -1;
    NBTTagList tagList = tagCompound.getTagList(TAG_LIST_NAME, Constants.NBT.TAG_COMPOUND);
    
    for (int i = 0; i < tagList.tagCount(); i++)
    {
      NBTTagCompound entryCompound = (NBTTagCompound) tagList.get(i);
      int key = entryCompound.getInteger("NetworkId");

      if (key != currentKey)
      {
        currentKey = key;
        autoChestRegistry.put(key, new LinkedHashSet<BlockPos>());
      }
      BlockPos pos = BlockPos.fromLong(entryCompound.getLong("Position"));
      
      if (!reverseLookup.containsKey(pos))
      {
        autoChestRegistry.get(key).add(pos);
        reverseLookup.put(pos, key);
      }
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
  {
    NBTTagList tagList = new NBTTagList();
    for (Integer key : autoChestRegistry.keySet())
    {
      Set<BlockPos> entries = autoChestRegistry.get(key);
      for (BlockPos pos : entries)
      {
        NBTTagCompound entryCompound = new NBTTagCompound();
        entryCompound.setInteger("NetworkId", key);
        entryCompound.setLong("Position", pos.toLong());
        tagList.appendTag(entryCompound);
      }
    }
    tagCompound.setTag(TAG_LIST_NAME, tagList);
    return tagCompound;
  }

  public static AutoChestRegistry get(World world)
  {
    MapStorage storage = world.getMapStorage();
    if (storage != null)
    {
      AutoChestRegistry instance = (AutoChestRegistry) storage.getOrLoadData(AutoChestRegistry.class, DATA_NAME);
      if (instance == null)
      {
        instance = new AutoChestRegistry();
        storage.setData(DATA_NAME, instance);
      }
      return instance;
    }
    return new AutoChestRegistry();
  }
}
