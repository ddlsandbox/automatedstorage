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
package automatedstorage.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerFilter extends ItemStackHandler
{
  public ItemStackHandlerFilter(int slots)
  {
    super(slots);
  }

  public NonNullList<ItemStack> getItems()
  {
    return this.stacks;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
  {
//    super.insertItem(slot, ItemHandlerHelper.copyStackWithSize(stack, 1), simulate);
//    return stack;
    if (!StackUtil.isValid(stack))
    {
      return StackUtil.getNull();
    }
    this.validateSlotIndex(slot);

    if (canInsert(stack, slot)) //) && !simulate)
    {
      this.stacks.set(slot, 
         ItemHandlerHelper.copyStackWithSize(stack, 1));
    }
    
    return stack;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate)
  {
    this.stacks.set(slot, StackUtil.getNull());
    return StackUtil.getNull();
  }

  public boolean canInsert(ItemStack stack, int insertSlot)
  {
    for (int slot=0; slot<getSlots(); ++slot)
    {
      // TODO: Compare item or stack?
      if (this.stacks.get(slot).getItem().equals(stack.getItem()))
        return false;
    }
    return true;
  }
  
  public boolean canExtract(ItemStack stack, int slot)
  {
    return true;
  }
  
  @Override
  public int getSlotLimit(int slot)
  {
    return 1;
  }
}
