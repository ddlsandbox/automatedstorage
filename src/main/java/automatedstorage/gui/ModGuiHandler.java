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
package automatedstorage.gui;

import automatedstorage.container.ContainerAutoChest;
import automatedstorage.container.ContainerAutoChestConfig;
import automatedstorage.tileentity.TileEntityAutoChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler
{
  public static final int AUTOCHEST = 0;
  public static final int AUTOCHEST_CONFIG = 1;

  @Override
  public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
    switch (ID)
    {
    case AUTOCHEST:
      return new ContainerAutoChest(player.inventory, (TileEntityAutoChest) world.getTileEntity(new BlockPos(x, y, z)));
    case AUTOCHEST_CONFIG:
      return new ContainerAutoChestConfig(player.inventory, (TileEntityAutoChest) world.getTileEntity(new BlockPos(x, y, z)));
    default:
      return null;
    }
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
    switch (ID)
    {
    case AUTOCHEST:
      return new AutoChestGui((TileEntityAutoChest) world.getTileEntity(new BlockPos(x, y, z)),
          new ContainerAutoChest(player.inventory, (TileEntityAutoChest) world.getTileEntity(new BlockPos(x, y, z))));
    case AUTOCHEST_CONFIG:
      return new AutoChestConfigGui((TileEntityAutoChest) world.getTileEntity(new BlockPos(x, y, z)),
          new ContainerAutoChestConfig(player.inventory, (TileEntityAutoChest) world.getTileEntity(new BlockPos(x, y, z))),
          player.inventory);
    default:
      return null;
    }
  }

}
