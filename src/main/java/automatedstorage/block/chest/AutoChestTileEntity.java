package automatedstorage.block.chest;

import java.util.List;

import automatedstorage.item.ItemStackHandlerCustom;
import automatedstorage.tileentity.TileEntityInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class AutoChestTileEntity extends TileEntityInventory implements ITickable
{
  private int networkId;

  public static final int AUTOCHEST_ROWS = 6;
  public static final int AUTOCHEST_COLS = 9;
  public static final int AUTOCHEST_SIZE = AUTOCHEST_ROWS * AUTOCHEST_COLS;

  public static final int AUTOCHEST_FILTER_ROWS = 3;
  public static final int AUTOCHEST_FILTER_COLS = 9;
  public static final int AUTOCHEST_FILTER_SIZE = AUTOCHEST_FILTER_ROWS * AUTOCHEST_FILTER_COLS;

  public AutoChestTileEntity()
  {
    super("tile.autochest.name", AUTOCHEST_SIZE + AUTOCHEST_FILTER_SIZE, 0);

    this.networkId = 0;
  }

  public int getNetworkId()
  {
    return networkId;
  }

  public void setNetworkId(int networkId)
  {
    this.networkId = networkId;
    this.markDirty();
  }
  
  protected ItemStackHandlerCustom getSlots()
  {
    return new ItemStackHandlerCustom(AUTOCHEST_SIZE + AUTOCHEST_FILTER_SIZE)
    {
      @Override
      protected void onContentsChanged(int slot)
      {
        super.onContentsChanged(slot);
        AutoChestTileEntity.this.markDirty();
      }

      @Override
      public int getSlotLimit(int slot)
      {
        return (slot >= AUTOCHEST_SIZE) ? 1 : super.getSlotLimit(slot);
      }
    };
  }

  @Override
  public void writeSyncableNBT(NBTTagCompound compound, NBTType type)
  {
    compound.setInteger("networkId", this.networkId);
    super.writeSyncableNBT(compound, type);
  }

  @Override
  public void readSyncableNBT(NBTTagCompound compound, NBTType type)
  {
    this.networkId = compound.getInteger("networkId");
    super.readSyncableNBT(compound, type);
  }
  
  public void redistribute()
  {
    AutoChestRegistry autoChestRegistry = AutoChestRegistry
        .get(this.getWorld());
    List<BlockPos> others = autoChestRegistry.getAutoChests(this.networkId);
    //TODO
//    System.out.println("AUTOCHESTS UPDATE " + networkId + "/" + getPos() + " together with ");
//    System.out.println(" --> " + others);
//    for (BlockPos otherPos : others)
//    {
//      System.out.println(" " + otherPos);
//    }
//    System.out.println(" END ");  
  }
  
  @Override
  public void updateEntity()
  {
    super.updateEntity();
  }
}