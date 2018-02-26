package automatedstorage.block.chest;

import javax.annotation.Nullable;

import automatedstorage.container.ContainerBase;
import automatedstorage.item.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AutoChestContainer extends ContainerBase
{

  private AutoChestTileEntity te;

  private static final int AUTOCHEST_INV_HEIGHT = 126;

  public AutoChestContainer(InventoryPlayer playerInventory, AutoChestTileEntity te)
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

    for (int i = 0; i < AutoChestTileEntity.AUTOCHEST_ROWS; i++)
    {
      for (int j = 0; j < AutoChestTileEntity.AUTOCHEST_COLS; j++)
      {
        this.addSlotToContainer(
            new SlotItemHandler(itemHandler, j + i * AutoChestTileEntity.AUTOCHEST_COLS, 
                10 + j * 18, 
                10 + i * 18)
            {
              @Override
              public boolean isItemValid(ItemStack stack)
              {
                /* allow only if item exists in filter */
                for (int slotId = 0; slotId < AutoChestTileEntity.AUTOCHEST_FILTER_SIZE; slotId++)
                {
                  ItemStack checkStack = te.getStackInSlot(AutoChestTileEntity.AUTOCHEST_SIZE + slotId);
                  if (StackUtil.isValid(checkStack) && checkStack.getItem() == stack.getItem())
                    return true;
                }
                return false;
              }
              
              @Override
              public void onSlotChanged()
              {
                super.onSlotChanged();
                te.redistribute();
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
            customFirstSlotIndex + AutoChestTileEntity.AUTOCHEST_SIZE, false))
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