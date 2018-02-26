package automatedstorage.gui;

import automatedstorage.block.chest.AutoChestConfigContainer;
import automatedstorage.block.chest.AutoChestContainer;
import automatedstorage.block.chest.AutoChestTileEntity;
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
      return new AutoChestContainer(player.inventory, (AutoChestTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
    case AUTOCHEST_CONFIG:
      return new AutoChestConfigContainer(player.inventory, (AutoChestTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
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
      return new AutoChestGui((AutoChestTileEntity) world.getTileEntity(new BlockPos(x, y, z)),
          new AutoChestContainer(player.inventory, (AutoChestTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
    case AUTOCHEST_CONFIG:
      return new AutoChestConfigGui((AutoChestTileEntity) world.getTileEntity(new BlockPos(x, y, z)),
          new AutoChestConfigContainer(player.inventory, (AutoChestTileEntity) world.getTileEntity(new BlockPos(x, y, z))),
          player.inventory);
    default:
      return null;
    }
  }

}
