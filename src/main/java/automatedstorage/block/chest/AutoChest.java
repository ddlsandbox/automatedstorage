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
package automatedstorage.block.chest;

import automatedstorage.AutomatedStorage;
import automatedstorage.block.BlockTileEntity;
import automatedstorage.gui.ModGuiHandler;
import automatedstorage.item.ModItems;
import automatedstorage.network.AutoChestRegistry;
import automatedstorage.tileentity.TileEntityAutoChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AutoChest extends BlockTileEntity<TileEntityAutoChest>
{
  
  public AutoChest(String name)
  {
    super(Material.IRON, name);
  }
  
  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
      EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (player.getHeldItem(hand).getItem() == ModItems.configurator)
      return false;

    if (worldIn.isRemote)
      return true;

    if (player.isSneaking())
    {
      player.openGui(AutomatedStorage.instance, ModGuiHandler.AUTOCHEST_CONFIG, worldIn, pos.getX(), pos.getY(),
          pos.getZ());
    } else
    {
      player.openGui(AutomatedStorage.instance, ModGuiHandler.AUTOCHEST, worldIn, pos.getX(), pos.getY(), pos.getZ());
    }
    return true;
  }

  private void cleanNetwork(World world, BlockPos pos)
  {
    AutoChestRegistry autoChestRegistry = AutoChestRegistry.get(world);
    autoChestRegistry.removeAutoChest(pos);
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState blockState, EntityLivingBase entity,
      ItemStack stack)
  {
    super.onBlockPlacedBy(world, pos, blockState, entity, stack);
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) 
  {
    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity instanceof IInventory) {
      InventoryHelper.dropInventoryItems(world, pos, (IInventory)tileEntity);
    }
    cleanNetwork(world, pos);
  }

  @Override
  public Class<TileEntityAutoChest> getTileEntityClass()
  {
    return TileEntityAutoChest.class;
  }

  @Override
  public TileEntity createNewTileEntity(World world, int arg1)
  {
    return new TileEntityAutoChest();
  }
}