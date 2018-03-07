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

public class ItemStackHandlerCustom extends ItemStackHandler
{

  private boolean tempIgnoreConditions;

  public ItemStackHandlerCustom(int slots)
  {
    super(slots);
  }

  public void decrStackSize(int slot, int amount)
  {
    this.setStackInSlot(slot, StackUtil.addStackSize(this.getStackInSlot(slot), -amount));
  }

  public NonNullList<ItemStack> getItems()
  {
    return this.stacks;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
  {
    if (!StackUtil.isValid(stack))
    {
      return StackUtil.getNull();
    }
    this.validateSlotIndex(slot);

    ItemStack existing = this.stacks.get(slot);

    int limit = this.getStackLimit(slot, stack);
    if (StackUtil.isValid(existing))
    {
      if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
      {
        return stack;
      }
      limit -= existing.getCount();
    }
    if (limit <= 0)
    {
      return stack;
    }

    if (!this.tempIgnoreConditions && !this.canInsert(stack, slot))
    {
      return stack;
    }

    boolean reachedLimit = stack.getCount() > limit;
    if (!simulate)
    {
      if (!StackUtil.isValid(existing))
      {
        this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
      } else
      {
        existing.grow(reachedLimit ? limit : stack.getCount());
      }

      this.onContentsChanged(slot);
    }

    return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;

  }

  public ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate)
  {
    this.tempIgnoreConditions = true;
    ItemStack result = this.insertItem(slot, stack, simulate);
    this.tempIgnoreConditions = false;
    return result;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate)
  {
    if (amount <= 0)
    {
      return StackUtil.getNull();
    }
    this.validateSlotIndex(slot);

    ItemStack existing = this.stacks.get(slot);
    if (!StackUtil.isValid(existing))
    {
      return StackUtil.getNull();
    }

    int toExtract = Math.min(amount, existing.getMaxStackSize());
    if (toExtract <= 0)
    {
      return StackUtil.getNull();
    }

    if (!this.tempIgnoreConditions && !this.canExtract(this.getStackInSlot(slot), slot))
    {
      return StackUtil.getNull();
    }

    if (existing.getCount() <= toExtract)
    {
      if (!simulate)
      {
        this.stacks.set(slot, StackUtil.getNull());
        this.onContentsChanged(slot);
        return existing;
      }
      return existing.copy();
    } else
    {
      if (!simulate)
      {
        this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
        this.onContentsChanged(slot);
      }
      return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
    }
  }

  public ItemStack extractItemInternal(int slot, int amount, boolean simulate)
  {
    this.tempIgnoreConditions = true;
    ItemStack result = this.extractItem(slot, amount, simulate);
    this.tempIgnoreConditions = false;
    return result;
  }

  public boolean canInsert(ItemStack stack, int slot)
  {
    return true;
  }

  public boolean canExtract(ItemStack stack, int slot)
  {
    return true;
  }
}
