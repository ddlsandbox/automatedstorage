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
package automatedstorage.block;

import automatedstorage.block.chest.AutoChest;
import automatedstorage.block.chest.ColoredAutoChest;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks
{

  @GameRegistry.ObjectHolder("automatedstorage:autochest")
  public static ColoredAutoChest autoChest;
  
  @GameRegistry.ObjectHolder("automatedstorage:autochest_source")
  public static AutoChest autoChestSource;
  
  @GameRegistry.ObjectHolder("automatedstorage:autochest_sink")
  public static AutoChest autoChestSink;
  
  public static void init() {
    
  }
  
  @SideOnly(Side.CLIENT)
  public static void initModels() {
    autoChest.initModel();
    autoChestSource.initModel();
    autoChestSink.initModel();
  }
}
