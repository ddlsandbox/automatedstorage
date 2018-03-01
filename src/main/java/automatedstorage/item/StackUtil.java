package automatedstorage.item;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public final class StackUtil
{
  public static ItemStack validateCopy(ItemStack stack)
  {
    if (isValid(stack))
    {
      return stack.copy();
    } else
    {
      return getNull();
    }
  }

  public static ItemStack validateCheck(ItemStack stack)
  {
    if (isValid(stack))
    {
      return stack;
    } else
    {
      return getNull();
    }
  }

  public static boolean isNotNull(ItemStack stack)
  {
    return stack != null && !ItemStack.areItemStacksEqual(stack, getNull()) && stack.getItem() != null;
  }

  public static boolean isValid(ItemStack stack)
  {
    return stack != null && !ItemStack.areItemStacksEqual(stack, getNull()) && stack.getCount() > 0
        && stack.getItem() != null;
  }

  public static ItemStack getNull()
  {
    return ItemStack.EMPTY;
  }

  public static int getStackSize(ItemStack stack)
  {
    if (!isValid(stack))
    {
      return 0;
    } else
    {
      return stack.getCount();
    }
  }

  public static ItemStack setStackSize(ItemStack stack, int size)
  {
    return setStackSize(stack, size, false);
  }

  public static ItemStack setStackSize(ItemStack stack, int size, boolean containerOnEmpty)
  {
    if (size <= 0)
    {
      if (isValid(stack) && containerOnEmpty)
      {
        return stack.getItem().getContainerItem(stack);
      } else
      {
        return getNull();
      }
    }
    stack.setCount(size);
    return stack;
  }

  public static ItemStack addStackSize(ItemStack stack, int size)
  {
    return addStackSize(stack, size, false);
  }

  public static ItemStack addStackSize(ItemStack stack, int size, boolean containerOnEmpty)
  {
    return setStackSize(stack, getStackSize(stack) + size, containerOnEmpty);
  }

  public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2, boolean checkWildcard)
  {
    return isNotNull(stack1) && isNotNull(stack2) && (stack1.isItemEqual(stack2));
  }
  
  /**
   * Can insert the specified item from the specified slot on the specified side?
   */
  public static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
  {
    return !inventoryIn.isItemValidForSlot(index, stack) ? false
        : !(inventoryIn instanceof ISidedInventory)
            || ((ISidedInventory) inventoryIn).canInsertItem(index, stack, side);
  }

  /**
   * Can combine 2 stacks?
   */
  public static boolean canCombine(ItemStack stack1, ItemStack stack2)
  {
    return stack1.getItem() != stack2.getItem() ? false
        : (stack1.getMetadata() != stack2.getMetadata() ? false
            : (stack1.getCount() > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2)));
  }
}
