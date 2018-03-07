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
package automatedstorage.container;

import javax.annotation.Nullable;

import automatedstorage.item.StackUtil;
import automatedstorage.tileentity.TileEntityAutoChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerAutoChestConfig extends ContainerBase
{

  private TileEntityAutoChest te;

  private static final int AUTOCHEST_INV_HEIGHT = 90;
  private static final int CUSTOM_INV_X = 8;
  private static final int CUSTOM_INV_Y = 28;
  private static final int TILE_SIZE = 18;

  public ContainerAutoChestConfig(InventoryPlayer playerInventory, TileEntityAutoChest te)
  {
    super(true, false);

    this.te = te;

    guiHotbarPosX = 8;
    guiHotbarPosY = AUTOCHEST_INV_HEIGHT + 62;
    guiInventoryPosX = 8;
    guiInventoryPosY = AUTOCHEST_INV_HEIGHT + 4;

    addVanillaSlots(playerInventory);

    addOwnSlots();
  }

  private void addOwnSlots()
  {
    IItemHandler itemHandler = this.te.filter;

    /* item filter */
    
    for (int i = 0; i < TileEntityAutoChest.AUTOCHEST_FILTER_ROWS; i++)
    {
      for (int j = 0; j < TileEntityAutoChest.AUTOCHEST_FILTER_COLS; j++)
      {
        this.addSlotToContainer(new SlotItemHandler(itemHandler,
            j + i * TileEntityAutoChest.AUTOCHEST_FILTER_COLS, 
            CUSTOM_INV_X + j * TILE_SIZE,
            CUSTOM_INV_Y + i * TILE_SIZE)
        {

          /* set a maximum of 1 per slot */
          @Override
          public int getSlotStackLimit()
          {
            return 1;
          }

          @Override
          public boolean isItemValid(ItemStack stack)
          {
            return te.filter.canInsert(stack, 0);
          }
        });
      }
    }
  }
  
  @Override
  public void putStackInSlot(int slot, ItemStack stack)
  {
    super.putStackInSlot(slot, stack);
    this.te.markDirty();
  }
  
  @Nullable
  @Override
  public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
  {
    Slot theSlot = inventorySlots.get(index);
    
    if (theSlot != null && theSlot.getHasStack())
    {
      ItemStack sourceStack = theSlot.getStack();
      ItemStack copyOfSourceStack = sourceStack.copy();
      this.te.markDirty();
      
      if (isVanillaSlot(index))
      {
        /* try putting in filter */
        if (!this.mergeItemStack(sourceStack, customFirstSlotIndex,
              customFirstSlotIndex + TileEntityAutoChest.AUTOCHEST_FILTER_SIZE, false))
        {
          return ItemStack.EMPTY;
        }
      }
      else
      {
        return ItemStack.EMPTY;
      }

      if (!isValid(sourceStack))
      {
        theSlot.putStack(ItemStack.EMPTY);
      } else
      {
        theSlot.onSlotChanged();
      }

      return copyOfSourceStack;
    }
    return ItemStack.EMPTY;
  }

  @Override
  protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean backwards)
  {
    boolean flag1 = false;
    int k = (backwards ? end - 1 : start);

    k = (backwards ? end - 1 : start);
    while (!backwards && k < end || backwards && k >= start)
    {
      Slot slot = (Slot) inventorySlots.get(k);
      ItemStack itemstack1 = slot.getStack();

      if (!slot.isItemValid(stack))
      {
        k += (backwards ? -1 : 1);
        continue;
      }

      if (!StackUtil.isValid(itemstack1))
      {
        slot.putStack(ItemHandlerHelper.copyStackWithSize(stack, 1));
        slot.onSlotChanged();
        flag1 = true;
        break;
      }

      k += (backwards ? -1 : 1);
    }

    return flag1;
  }
  
  public void updateNetwork(int newNetwork)
  {
    te.setNetworkId(newNetwork);
  }
  
  @Override
  public boolean canInteractWith(EntityPlayer playerIn)
  {
    return te.isUsableByPlayer(playerIn);
  }

  public static boolean isValid(ItemStack stack)
  {
    return stack != null && !ItemStack.areItemStacksEqual(stack, ItemStack.EMPTY) && stack.getCount() > 0
        && stack.getItem() != null;
  }
}