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
package automatedstorage.item;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

public final class ItemUtil
{
  public static final String DATA_ID = "autochestsData";
  
  public static Item getItemFromName(String name)
  {
    ResourceLocation resLoc = new ResourceLocation(name);
    if (Item.REGISTRY.containsKey(resLoc))
    {
      return Item.REGISTRY.getObject(resLoc);
    }
    return null;
  }

  public static int getPlaceAt(ItemStack[] array, ItemStack stack)
  {
    return getPlaceAt(Arrays.asList(array), stack);
  }

  /**
   * Returns true if array contains stack or if both contain null
   */
  public static boolean contains(ItemStack[] array, ItemStack stack)
  {
    return getPlaceAt(array, stack) != -1;
  }

  
  /**
   * Returns the place of stack in array, -1 if not present
   */
  public static int getPlaceAt(List<ItemStack> list, ItemStack stack)
  {
    if (list != null && list.size() > 0)
    {
      for (int i = 0; i < list.size(); i++)
      {
        if ((!StackUtil.isValid(stack) && !StackUtil.isValid(list.get(i)))
            || areItemsEqual(stack, list.get(i)))
        {
          return i;
        }
      }
    }
    return -1;
  }

  public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2)
  {
    return StackUtil.isValid(stack1) && StackUtil.isValid(stack2)
        && (stack1.isItemEqual(stack2));
  }

  /**
   * Returns true if list contains stack or if both contain null
   */
  public static boolean contains(List<ItemStack> list, ItemStack stack)
  {
    return !(list == null || list.isEmpty())
        && getPlaceAt(list, stack) != -1;
  }

  public static boolean canBeStacked(ItemStack stack1, ItemStack stack2)
  {
    return ItemStack.areItemsEqual(stack1, stack2)
        && ItemStack.areItemStackTagsEqual(stack1, stack2);
  }
  
  /**** DATA UTILS ****/
  
  public static NBTTagCompound getDataMap(ItemStack stack)
  {
    initStack(stack);
    
    return stack.getTagCompound().getCompoundTag(DATA_ID);
  }
  
  public static boolean hasData(ItemStack stack, String key)
  {
    initStack(stack);
    
    return getDataMap(stack).hasKey(key);
  }
  
  public static void removeData(ItemStack stack, String key)
  {
    initStack(stack);
    
    getDataMap(stack).removeTag(key);
  }
  
  public static int getInt(ItemStack stack, String key)
  {
    initStack(stack);
    
    return getDataMap(stack).getInteger(key);
  }
  
  public static boolean getBoolean(ItemStack stack, String key)
  {
    initStack(stack);
    
    return getDataMap(stack).getBoolean(key);
  }
  
  public static double getDouble(ItemStack stack, String key)
  {
    initStack(stack);
    
    return getDataMap(stack).getDouble(key);
  }
  
  public static String getString(ItemStack stack, String key)
  {
    initStack(stack);
    
    return getDataMap(stack).getString(key);
  }
  
  public static NBTTagCompound getCompound(ItemStack stack, String key)
  {
    initStack(stack);
    
    return getDataMap(stack).getCompoundTag(key);
  }
  
  public static NBTTagList getList(ItemStack stack, String key)
  {
    initStack(stack);
    
    return getDataMap(stack).getTagList(key, NBT.TAG_COMPOUND);
  }
  
  public static void setInt(ItemStack stack, String key, int i)
  {
    initStack(stack);
    
    getDataMap(stack).setInteger(key, i);
  }
  
  public static void setBoolean(ItemStack stack, String key, boolean b)
  {
    initStack(stack);
    
    getDataMap(stack).setBoolean(key, b);
  }
  
  public static void setDouble(ItemStack stack, String key, double d)
  {
    initStack(stack);
    
    getDataMap(stack).setDouble(key, d);
  }
  
  public static void setString(ItemStack stack, String key, String s)
  {
    initStack(stack);
    
    getDataMap(stack).setString(key, s);
  }
  
  public static void setCompound(ItemStack stack, String key, NBTTagCompound tag)
  {
    initStack(stack);
    
    getDataMap(stack).setTag(key, tag);
  }
  
  public static void setList(ItemStack stack, String key, NBTTagList tag)
  {
    initStack(stack);
    
    getDataMap(stack).setTag(key, tag);
  }
  
  private static void initStack(ItemStack stack)
  {
    if(stack.getTagCompound() == null)
    {
      stack.setTagCompound(new NBTTagCompound());
    }
    
    if(!stack.getTagCompound().hasKey(DATA_ID))
    {
      stack.getTagCompound().setTag(DATA_ID, new NBTTagCompound());
    }
  }
}