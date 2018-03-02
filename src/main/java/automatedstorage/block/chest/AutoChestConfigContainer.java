package automatedstorage.block.chest;

import javax.annotation.Nullable;

import automatedstorage.container.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AutoChestConfigContainer extends ContainerBase
{

  private AutoChestTileEntity te;

  private static final int AUTOCHEST_INV_HEIGHT = 126;

  public AutoChestConfigContainer(InventoryPlayer playerInventory, AutoChestTileEntity te)
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

    for (int i = 0; i < AutoChestTileEntity.AUTOCHEST_FILTER_ROWS; i++)
    {
      for (int j = 0; j < AutoChestTileEntity.AUTOCHEST_FILTER_COLS; j++)
      {
        this.addSlotToContainer(new SlotItemHandler(itemHandler,
            j + i * AutoChestTileEntity.AUTOCHEST_FILTER_COLS, 
            10 + j * 18,
            28 + i * 18)
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
            customFirstSlotIndex + AutoChestTileEntity.AUTOCHEST_FILTER_SIZE, false))
        {
          return ItemStack.EMPTY;
        }
      } else
      {
        if (!this.mergeItemStack(sourceStack, vanillaFirstSlotIndex, vanillaFirstSlotIndex + vanillaSlotCount, false))
        {
          return ItemStack.EMPTY;
        }
      }

      if (!isValid(sourceStack))
      {
        theSlot.putStack(ItemStack.EMPTY);
      } else
      {
        theSlot.onSlotChanged();
      }

      if (getStackSize(sourceStack) == getStackSize(copyOfSourceStack))
      {
        return ItemStack.EMPTY;
      }
      theSlot.onTake(playerIn, sourceStack);

      return copyOfSourceStack;
    }
    return ItemStack.EMPTY;
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
}