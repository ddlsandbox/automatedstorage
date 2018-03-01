package automatedstorage.block.chest;

import automatedstorage.AutomatedStorage;
import automatedstorage.block.BlockTileEntity;
import automatedstorage.gui.ModGuiHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AutoChest extends BlockTileEntity<AutoChestTileEntity>
{

  public static final int GUI_ID = 1;

  public AutoChest(String name)
  {
    super(Material.ROCK, name);
  }

  @SideOnly(Side.CLIENT)
  public void initModel()
  {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
        new ModelResourceLocation(getRegistryName(), "inventory"));
  }

  @Override
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
      EnumFacing side, float hitX, float hitY, float hitZ)
  {
    if (worldIn.isRemote)
      return true;

    if (playerIn.isSneaking())
    {
      playerIn.openGui(AutomatedStorage.instance, ModGuiHandler.AUTOCHEST_CONFIG, worldIn, pos.getX(), pos.getY(),
          pos.getZ());
    } else
    {
      playerIn.openGui(AutomatedStorage.instance, ModGuiHandler.AUTOCHEST, worldIn, pos.getX(), pos.getY(), pos.getZ());
    }
    return true;
  }

  @Override
  public Class<AutoChestTileEntity> getTileEntityClass()
  {
    return AutoChestTileEntity.class;
  }

  @Override
  public TileEntity createNewTileEntity(World world, int arg1)
  {
    return new AutoChestTileEntity();
  }
}