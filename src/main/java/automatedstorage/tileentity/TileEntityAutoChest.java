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
package automatedstorage.tileentity;

import java.util.LinkedHashSet;
import java.util.Set;

import automatedstorage.AutomatedStorage;
import automatedstorage.block.ModBlocks;
import automatedstorage.item.ItemStackHandlerCustom;
import automatedstorage.item.ItemStackHandlerFilter;
import automatedstorage.item.StackUtil;
import automatedstorage.network.AutoChestRegistry;
import automatedstorage.network.PacketUpdateRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class TileEntityAutoChest extends TileEntityInventory implements ITickable
{
  private int networkId;
  private int transferCooldown = -1;
  private boolean isEmpty = true;

  public ItemStackHandlerFilter filter;
  
  public static final int AUTOCHEST_ROWS = 6;
  public static final int AUTOCHEST_COLS = 9;
  public static final int AUTOCHEST_SIZE = AUTOCHEST_ROWS * AUTOCHEST_COLS;

  public static final int AUTOCHEST_FILTER_ROWS = 3;
  public static final int AUTOCHEST_FILTER_COLS = 9;
  public static final int AUTOCHEST_FILTER_SIZE = AUTOCHEST_FILTER_ROWS * AUTOCHEST_FILTER_COLS;
  
  public TileEntityAutoChest()
  {
    super("tile.autochest.name", AUTOCHEST_SIZE, 0);
    filter = new ItemStackHandlerFilter(AUTOCHEST_FILTER_SIZE);
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
      autoChestRegistry.addAutoChest(networkId, getPos(), getBlockType() == ModBlocks.autoChestSink?1:0);
    }
    
    this.markDirty();
  }

  protected ItemStackHandlerCustom getSlots()
  {
    return new ItemStackHandlerCustom(AUTOCHEST_SIZE)
    {
      @Override
      public boolean canInsert(ItemStack stack, int slot)
      {
        return TileEntityAutoChest.this.isItemValidForSlot(slot, stack);
      }

      @Override
      protected void onContentsChanged(int slot)
      {
        super.onContentsChanged(slot);
        TileEntityAutoChest.this.isEmpty = false;
        TileEntityAutoChest.this.markDirty();
      }
    };
  }

  @Override
  public void writeSyncableNBT(NBTTagCompound compound, NBTType type)
  {
    compound.setInteger("NetworkId", this.networkId);
    compound.setInteger("TransferCooldown", this.transferCooldown);
    compound.setBoolean("Empty", this.isEmpty);
    
    /* item filter */
    
    NBTTagList dataForFilterSlots = new NBTTagList();
    for (int i = 0; i < AUTOCHEST_FILTER_SIZE; ++i)
    {
      if (!filter.getStackInSlot(i).isEmpty())
      {
        NBTTagCompound dataForThisSlot = new NBTTagCompound();
        dataForThisSlot.setByte("FilterSlot", (byte) i);
        filter.getStackInSlot(i).writeToNBT(dataForThisSlot);
        dataForFilterSlots.appendTag(dataForThisSlot);
      }
    }
    compound.setTag("FilterItems", dataForFilterSlots);
    
    super.writeSyncableNBT(compound, type);
  }

  @Override
  public void readSyncableNBT(NBTTagCompound compound, NBTType type)
  {
    final byte NBT_TYPE_COMPOUND = 10; // See NBTBase.createNewByType() for a listing
    
    this.networkId = compound.getInteger("NetworkId");
    this.transferCooldown = compound.getInteger("TransferCooldown");
    this.isEmpty = compound.getBoolean("Empty");
    
    /* item filter */
    
    NBTTagList dataForFilterSlots = compound.getTagList("FilterItems", NBT_TYPE_COMPOUND);
    for (int i = 0; i < dataForFilterSlots.tagCount(); ++i)
    {
      NBTTagCompound dataForOneSlot = dataForFilterSlots.getCompoundTagAt(i);
      byte slotNumber = dataForOneSlot.getByte("FilterSlot");
      if (slotNumber >= 0 && slotNumber < AUTOCHEST_FILTER_SIZE)
      {
        filter.setStackInSlot(slotNumber, new ItemStack(dataForOneSlot));
      }
    }
    
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
    IInventory returnEntity = null;
    boolean doClean = false;
    Set<BlockPos> cleanList = null;
    AutoChestRegistry autoChestRegistry = AutoChestRegistry.get(this.getWorld());
    Set<BlockPos> others = autoChestRegistry.getAutoChests(this.networkId);
    for (BlockPos otherPos : others)
    {
      if (!otherPos.equals(getPos()))
      {
        IInventory entity = (IInventory) world.getTileEntity(otherPos);

        if (entity == null)
        {
          doClean = true;
          if (cleanList == null)
            cleanList = new LinkedHashSet<BlockPos>();
          cleanList.add(otherPos);
        }
        else if(entity.isItemValidForSlot(0, stack))
        {
          returnEntity = entity;
          break;
        }
      }
    }
    
    if (doClean)
    {
      for (BlockPos cleanPos : cleanList)
      {
        autoChestRegistry.removeAutoChest(cleanPos);
        if (!world.isRemote) {
          AutomatedStorage.network.sendToAllAround(
            new PacketUpdateRegistry(cleanPos, 0, -1), 
            new NetworkRegistry.TargetPoint(world.provider.getDimension(), 
              pos.getX(), pos.getY(), pos.getZ(), 64));
        }
      }
    }
    
    return returnEntity;
  }

  private boolean isItemInFilter(ItemStack stack)
  {
    return !filter.canInsert(stack, 0);
  }
  
  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack)
  {
    if (getBlockType() == ModBlocks.autoChestSource || getBlockType() == ModBlocks.autoChestSink)
      return true;
    
    return index >= AUTOCHEST_SIZE || isItemInFilter(stack); 
  }
  
  public boolean hasItemsForTransfer()
  {
    return !isEmpty();
  }
  
  @Override
  public boolean isEmpty()
  {
    if (this.isEmpty)
      return true;
    
    for (int i = 0; i < AUTOCHEST_SIZE; ++i)
      if (StackUtil.isNotNull(getStackInSlot(i)))
        return false;
    
    this.isEmpty = true;
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

        if (iinventory == null || this.isInventoryFull(iinventory))
        {
          /* slot cannot be transferred */
          continue;
        } 
        else
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
        if (inventoryIn instanceof TileEntityAutoChest)
        {
          TileEntityAutoChest tileentitychest = (TileEntityAutoChest) inventoryIn;

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