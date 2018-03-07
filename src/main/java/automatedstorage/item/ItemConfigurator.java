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

import automatedstorage.tileentity.TileEntityAutoChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemConfigurator extends ItemBase
{

  private static enum Mode { MODE_CLONE_NETWORK, MODE_SET_NETWORK };
  
  public ItemConfigurator(String name)
  {
    super(name);
    
    maxStackSize = 1;
  }

  @Override
  public ItemConfigurator setCreativeTab(CreativeTabs tab)
  {
    super.setCreativeTab(tab);
    return this;
  }

  @Override
  public int getItemStackLimit(ItemStack p_getItemStackLimit_1_)
  {
    return 1;
  }
  
  @Override
  public int getItemStackLimit()
  {
    return 1;
  }
  
  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world,
      BlockPos pos, EnumHand hand, EnumFacing side, float par8, float par9,
      float par10)
  {
    ItemStack stack = player.getHeldItem(hand);
    
    if (player.isSneaking())
    {
      /* switch mode */
      switch (getMode(stack))
      {
      case MODE_CLONE_NETWORK:
        setMode(stack, Mode.MODE_SET_NETWORK);
        break;
      case MODE_SET_NETWORK:
        setMode(stack, Mode.MODE_CLONE_NETWORK);
        break;
      }      
      
      if (world.isRemote)
      {
        player.sendMessage(new TextComponentString("Switch configurator mode to " + getMode(stack)));
      }
    }
    else
    {
      TileEntity tile = world.getTileEntity(pos);
      
      if (tile instanceof TileEntityAutoChest)
      {
        TileEntityAutoChest autochest = (TileEntityAutoChest) tile;
        
        switch (getMode(stack))
        {
        case MODE_CLONE_NETWORK:
          setStoredNetwork(stack, autochest.getNetworkId());
          if (world.isRemote)
          {
            player.sendMessage(new TextComponentString("Stored network " + getStoredNetwork(stack)));
          }
          break;
        case MODE_SET_NETWORK:
          autochest.setNetworkId(getStoredNetwork(stack));
          if (world.isRemote)
          {
            player.sendMessage(new TextComponentString("Network set to " + getStoredNetwork(stack)));
          }
          break;
        }  
        
        return EnumActionResult.SUCCESS;
      }
    }
    
    return super.onItemUse(player, world, pos, hand, side, par8, par9, par10);
  }

  public Mode getMode(ItemStack itemStack)
  {
    return Mode.values()[ItemUtil.getInt(itemStack, "ConfiguratorMode")];
  }
  
  public void setMode(ItemStack itemStack, Mode mode)
  {
    ItemUtil.setInt(itemStack, "ConfiguratorMode", mode.ordinal());
  }
  
  public int getStoredNetwork(ItemStack itemStack)
  {
    return ItemUtil.getInt(itemStack, "StoredNetwork");
  }
  
  public void setStoredNetwork(ItemStack itemStack, int networkId)
  {
    ItemUtil.setInt(itemStack, "StoredNetwork", networkId);
  }
  
  @Override
  public EnumRarity getRarity(ItemStack stack)
  {
    return EnumRarity.RARE;
  }

}
