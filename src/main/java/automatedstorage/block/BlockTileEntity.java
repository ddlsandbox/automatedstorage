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

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockTileEntity<TE extends TileEntity> extends BlockBase implements ITileEntityProvider {

  public BlockTileEntity(Material material, String name) {
    super(material, name);
    
    this.setHarvestLevel("pickaxe", 0);
    this.setHardness(1.5F);
    this.setResistance(10.0F);
    this.setSoundType(SoundType.STONE);
  }
  
  public abstract Class<TE> getTileEntityClass();
  
  @SuppressWarnings("unchecked")
  public TE getTileEntity(IBlockAccess world, BlockPos pos) {
    return (TE)world.getTileEntity(pos);
  }
  
  @Override
  public boolean hasTileEntity(IBlockState state) {
    return true;
  }
  
  @Override
  public abstract TileEntity createNewTileEntity(World worldIn, int meta);
  
}