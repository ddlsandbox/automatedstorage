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

import javax.annotation.Nullable;

import automatedstorage.item.ItemStackHandlerCustom;
import automatedstorage.item.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class TileEntityInventory extends TileEntityBase implements IInventory
{
  private final String name;

  protected final int inputSlotsCount;
  protected final int outputSlotsCount;
  protected final int totalSlotsCount;

  protected final int firstInputSlot, lastInputSlot;
  protected final int firstOutputSlot, lastOutputSlot;

  private ItemStackHandlerCustom slots;

  public TileEntityInventory(String name, int inputSlots, int outputSlots)
  {
    this.name = name;

    this.inputSlotsCount = inputSlots;
    this.outputSlotsCount = outputSlots;
    this.totalSlotsCount = inputSlotsCount + outputSlotsCount;

    this.firstInputSlot = 0;
    this.lastInputSlot = inputSlotsCount - 1;
    this.firstOutputSlot = inputSlotsCount;
    this.lastOutputSlot = firstOutputSlot + outputSlotsCount - 1;

    this.slots = this.getSlots();

    clear();
  }

  protected abstract ItemStackHandlerCustom getSlots();

  @Override
  public void openInventory(EntityPlayer player)
  {
  }

  @Override
  public void closeInventory(EntityPlayer arg0)
  {
  }

  @Override
  public void clear()
  {
    for (int i = 0; i < totalSlotsCount; ++i)
      slots.setStackInSlot(i, StackUtil.getNull());
  }

  @Override
  public boolean isEmpty()
  {
    for (int i = 0; i < totalSlotsCount; ++i)
      if (StackUtil.isNotNull(slots.getStackInSlot(i)))
        return false;
    return true;
  }

  /**
   * Removes some of the units from stack in the given slot, and returns as a
   * separate stack
   * 
   * @param slotIndex
   *          the slot number to remove the items from
   * @param count
   *          the number of units to remove
   * @return a new stack containing the units removed from the slot
   */
  @Override
  public ItemStack decrStackSize(int index, int count)
  {
    ItemStack itemStackInSlot = getStackInSlot(index);
    if (itemStackInSlot.isEmpty())
      return StackUtil.getNull();

    ItemStack itemStackRemoved;
    if (itemStackInSlot.getCount() <= count)
    {
      itemStackRemoved = itemStackInSlot;
      setInventorySlotContents(index, StackUtil.getNull());
    } else
    {
      itemStackRemoved = itemStackInSlot.splitStack(count);
      if (itemStackInSlot.getCount() == 0)
      {
        setInventorySlotContents(index, StackUtil.getNull());
      }
    }
    markDirty();
    return itemStackRemoved;
  }

  @Override
  public int getInventoryStackLimit()
  {
    return 64;
  }

  @Override
  public int getSizeInventory()
  {
    return slots.getSlots();
  }

  @Override
  public ItemStack getStackInSlot(int index)
  {
    return slots.getStackInSlot(index);
  }

  public void setStackInSlot(int index, ItemStack stack)
  {
    slots.setStackInSlot(index, stack);
  }

  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack)
  {
    return true;
  }

  @Override
  public boolean isUsableByPlayer(EntityPlayer player)
  {
    if (this.world.getTileEntity(this.pos) != this)
      return false;
    final double X_CENTRE_OFFSET = 0.5;
    final double Y_CENTRE_OFFSET = 0.5;
    final double Z_CENTRE_OFFSET = 0.5;
    final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
    return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET,
        pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
  }

  @Override
  public ItemStack removeStackFromSlot(int index)
  {
    ItemStack itemStack = getStackInSlot(index);
    if (!itemStack.isEmpty())
      setInventorySlotContents(index, ItemStack.EMPTY);
    return itemStack;
  }

  @Override
  public int getFieldCount()
  {
    return 0;
  }

  @Override
  public int getField(int index)
  {
    return 0;
  }

  @Override
  public void setField(int index, int value)
  {
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack)
  {
    slots.setStackInSlot(index, stack);
    if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
    {
      stack.setCount(getInventoryStackLimit());
    }
    markDirty();
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public boolean hasCustomName()
  {
    return false;
  }

  @Nullable
  @Override
  public ITextComponent getDisplayName()
  {
    return this.hasCustomName() ? new TextComponentString(this.getName())
        : new TextComponentTranslation(this.getName());
  }
  
  @Override
  public IItemHandler getItemHandler(EnumFacing facing)
  {
    return slots;
  }
  
  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing)
  {
    return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
  }

  /* NBT */

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound parentNBTTagCompound)
  {
    super.writeToNBT(parentNBTTagCompound);

    NBTTagList dataForAllSlots = new NBTTagList();
    for (int i = 0; i < totalSlotsCount; ++i)
    {
      if (!this.getStackInSlot(i).isEmpty())
      {
        NBTTagCompound dataForThisSlot = new NBTTagCompound();
        dataForThisSlot.setByte("Slot", (byte) i);
        this.getStackInSlot(i).writeToNBT(dataForThisSlot);
        dataForAllSlots.appendTag(dataForThisSlot);
      }
    }
    parentNBTTagCompound.setTag("Items", dataForAllSlots);
    return parentNBTTagCompound;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtTagCompound)
  {
    super.readFromNBT(nbtTagCompound);

    final byte NBT_TYPE_COMPOUND = 10; // See NBTBase.createNewByType() for a listing

    NBTTagList dataForAllSlots = nbtTagCompound.getTagList("Items", NBT_TYPE_COMPOUND);

    for (int i = 0; i < dataForAllSlots.tagCount(); ++i)
    {
      NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
      byte slotNumber = dataForOneSlot.getByte("Slot");
      if (slotNumber >= 0 && slotNumber < totalSlotsCount)
      {
        slots.setStackInSlot(slotNumber, new ItemStack(dataForOneSlot));
      }
    }
  }
}