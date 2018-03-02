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
    if (!StackUtil.isValid(stack))
    {
      return StackUtil.getNull();
    }
    this.validateSlotIndex(slot);

    if (canInsert(stack, slot) && !simulate)
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
