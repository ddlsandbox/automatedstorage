package automatedstorage.block;

import javax.annotation.Nullable;

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
  
//  @Nullable
//  @Override
//  public abstract TE createTileEntity(World world, IBlockState state);

  @Override
  public abstract TileEntity createNewTileEntity(World worldIn, int meta);
  
}