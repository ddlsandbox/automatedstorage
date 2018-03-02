package automatedstorage.block.chest;

import java.util.List;

import automatedstorage.block.ModBlocks;
import automatedstorage.item.ItemStackHandlerCustom;
import automatedstorage.item.StackUtil;
import automatedstorage.tileentity.TileEntityInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class AutoChestTileEntity extends TileEntityInventory implements ITickable
{
  private int networkId;
  private int transferCooldown = -1;

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
    
    if (getBlockType() != ModBlocks.autoChestSource)
    {
      AutoChestRegistry autoChestRegistry = AutoChestRegistry.get(this.getWorld());
      autoChestRegistry.addAutoChest(networkId, getPos());
    }
    
    this.markDirty();
  }

  protected ItemStackHandlerCustom getSlots()
  {
    return new ItemStackHandlerCustom(AUTOCHEST_SIZE + AUTOCHEST_FILTER_SIZE)
    {
      @Override
      public boolean canInsert(ItemStack stack, int slot)
      {
        return AutoChestTileEntity.this.isItemValidForSlot(slot, stack);
      }

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
    compound.setInteger("NetworkId", this.networkId);
    compound.setInteger("TransferCooldown", this.transferCooldown);
    super.writeSyncableNBT(compound, type);
  }

  @Override
  public void readSyncableNBT(NBTTagCompound compound, NBTType type)
  {
    this.networkId = compound.getInteger("NetworkId");
    this.transferCooldown = compound.getInteger("TransferCooldown");
    super.readSyncableNBT(compound, type);
  }

  /**
   * Finds the first chest in the network where the stack exists in the filter
   * 
   * Note that if several chests in the network contains the same filter, only
   * the first one will be used, and this implies that even if the first one is
   * full, the second one is never selected.
   */
  private IInventory getInventoryForTransfer(ItemStack stack)
  {
    AutoChestRegistry autoChestRegistry = AutoChestRegistry.get(this.getWorld());
    List<BlockPos> others = autoChestRegistry.getAutoChests(this.networkId);
    for (BlockPos otherPos : others)
    {
      if (!otherPos.equals(getPos()))
      {
        AutoChestTileEntity entity = (AutoChestTileEntity) world.getTileEntity(otherPos);
        if (entity.isItemValidForSlot(0, stack))
          return entity;
      }
    }
    return null;
  }

  private boolean isItemInFilter(ItemStack stack)
  {
    for (int slotId = 0; slotId < AUTOCHEST_FILTER_SIZE; slotId++)
    {
      ItemStack checkStack = getStackInSlot(AUTOCHEST_SIZE + slotId);
      if (StackUtil.isValid(checkStack) && checkStack.getItem() == stack.getItem())
        return true;
    }
    return false;  
  }
  
  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack)
  {
    if (getBlockType() == ModBlocks.autoChestSource)
      return (index < AUTOCHEST_SIZE);
    
    boolean itemInFilter = isItemInFilter(stack); 
    if (index < AUTOCHEST_SIZE)
    {
      return itemInFilter;
    }
    else
    {
      return !itemInFilter;
    }
  }
  
  public boolean hasItemsForTransfer()
  {
    return !isEmpty();
  }
  
  @Override
  public boolean isEmpty()
  {
    for (int i = 0; i < AUTOCHEST_SIZE; ++i)
      if (StackUtil.isNotNull(getStackInSlot(i)))
        return false;
    return true;
  }
  
  @Override
  public void updateEntity()
  {
    super.updateEntity();
    if (getBlockType() == ModBlocks.autoChestSource && this.world != null && !this.world.isRemote)
    {
      --this.transferCooldown;

      if (!this.isOnTransferCooldown())
      {
        this.setTransferCooldown(0);
        this.attemptTransfer();
      }
    }
  }

  public boolean attemptTransfer()
  {
    if (getBlockType() != ModBlocks.autoChestSource)
      return false;
    
    if (this.world != null && !this.world.isRemote)
    {
      if (!this.isOnTransferCooldown())
      {
        if (this.hasItemsForTransfer())
        {
          if (this.transferItemsOut())
          {
            this.setTransferCooldown(8);
            this.markDirty();
            return true;
          }
        }
      }

      return false;
    } else
    {
      return false;
    }
  }

  @Override
  public String toString()
  {
    return "Autochest[" + getPos() + "/" + networkId + "]";
  }
  
  private boolean transferItemsOut()
  {
    for (int i = 0; i < this.getSizeInventory(); ++i)
    {
      if (StackUtil.isValid(this.getStackInSlot(i)))
      {
        ItemStack itemstack = this.getStackInSlot(i).copy();
        IInventory iinventory = this.getInventoryForTransfer(itemstack);

        if (iinventory == null)
        {
          return false;
        } 
        else if (this.isInventoryFull(iinventory))
       {
          return false;
        } else
        {
          ItemStack itemstack1 = putStackInInventoryAllSlots(iinventory, this.decrStackSize(i, 1));

          if (!StackUtil.isValid(itemstack1))
          {
            iinventory.markDirty();
            return true;
          }

          this.setInventorySlotContents(i, itemstack);
        }
      }
    }
    return false;
  }

  /**
   * Returns false if the inventory has any room to place items in
   */
  private boolean isInventoryFull(IInventory inventoryIn)
  {
    int i = inventoryIn.getSizeInventory();

    for (int j = 0; j < i; ++j)
    {
      ItemStack itemstack = inventoryIn.getStackInSlot(j);

      if (!StackUtil.isValid(itemstack) || itemstack.getCount() != itemstack.getMaxStackSize())
      {
        return false;
      }
    }

    return true;
  }

  /**
   * Attempts to place the passed stack in the inventory, using as many slots as
   * required. Returns leftover items
   */
  public static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack)
  {
    int i = inventoryIn.getSizeInventory();

    for (int j = 0; j < i && StackUtil.isValid(stack) && stack.getCount() > 0; ++j)
    {
      stack = insertStack(inventoryIn, stack, j, null);
    }

    if (StackUtil.isValid(stack) && stack.getCount() == 0)
    {
      stack = null;
    }

    return stack;
  }

  /**
   * Insert the specified stack to the specified inventory and return any leftover
   * items
   */
  private static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
  {
    ItemStack itemstack = inventoryIn.getStackInSlot(index);

    if (StackUtil.canInsertItemInSlot(inventoryIn, stack, index, side))
    {
      boolean flag = false;

      if (!StackUtil.isValid(itemstack))
      {
        inventoryIn.setInventorySlotContents(index, stack);
        stack = null;
        flag = true;
      } else if (StackUtil.canCombine(itemstack, stack))
      {
        int i = stack.getMaxStackSize() - itemstack.getCount();
        int j = Math.min(stack.getCount(), i);
        stack.setCount(stack.getCount() - j);
        itemstack.setCount(itemstack.getCount() + j);
        flag = j > 0;
      }

      if (flag)
      {
        if (inventoryIn instanceof AutoChestTileEntity)
        {
          AutoChestTileEntity tileentitychest = (AutoChestTileEntity) inventoryIn;

          if (tileentitychest.mayTransfer())
          {
            tileentitychest.setTransferCooldown(8);
          }

          inventoryIn.markDirty();
        }

        inventoryIn.markDirty();
      }
    }

    return stack;
  }

  public void setTransferCooldown(int ticks)
  {
    this.transferCooldown = ticks;
  }

  public boolean isOnTransferCooldown()
  {
    return this.transferCooldown > 0;
  }

  public boolean mayTransfer()
  {
    return this.transferCooldown <= 1;
  }
}