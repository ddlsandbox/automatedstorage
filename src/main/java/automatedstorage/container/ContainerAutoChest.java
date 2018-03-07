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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerAutoChest extends ContainerBase
{

  private TileEntityAutoChest te;

  private static final int AUTOCHEST_INV_HEIGHT = 126;
  private static final int CUSTOM_INV_X = 8;
  private static final int CUSTOM_INV_Y = 8;
  private static final int TILE_SIZE = 18;

  public ContainerAutoChest(InventoryPlayer playerInventory, TileEntityAutoChest te)
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
    IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

    for (int i = 0; i < TileEntityAutoChest.AUTOCHEST_ROWS; i++)
    {
      for (int j = 0; j < TileEntityAutoChest.AUTOCHEST_COLS; j++)
      {
        this.addSlotToContainer(
            new SlotItemHandler(itemHandler, j + i * TileEntityAutoChest.AUTOCHEST_COLS, 
                CUSTOM_INV_X + j * TILE_SIZE, 
                CUSTOM_INV_Y + i * TILE_SIZE)
            {
              @Override
              public boolean isItemValid(ItemStack stack)
              {
                return te.isItemValidForSlot(0, stack);
              }
            });
      }
    }
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

      if (isVanillaSlot(index))
      {
        /* try putting in backpack */
        if (!this.mergeItemStack(sourceStack, customFirstSlotIndex,
            customFirstSlotIndex + TileEntityAutoChest.AUTOCHEST_SIZE, false))
        {
          return StackUtil.getNull();
        }
      } else
      {
        if (!this.mergeItemStack(sourceStack, vanillaFirstSlotIndex, vanillaFirstSlotIndex + vanillaSlotCount, false))
        {
          return StackUtil.getNull();
        }
      }
      
      if (!StackUtil.isValid(sourceStack))
      {
        theSlot.putStack(StackUtil.getNull());
      } else
      {
        theSlot.onSlotChanged();
      }

      if (StackUtil.getStackSize(sourceStack) == StackUtil.getStackSize(copyOfSourceStack))
      {
        return StackUtil.getNull();
      }
      theSlot.onTake(playerIn, sourceStack);

      return copyOfSourceStack;
    }
    return StackUtil.getNull();
  }

  @Override
  public boolean canInteractWith(EntityPlayer playerIn)
  {
    return te.isUsableByPlayer(playerIn);
  }
}