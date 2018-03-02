package automatedstorage.gui;

import automatedstorage.block.chest.AutoChestTileEntity;
import automatedstorage.item.StackUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAutoChestHud extends Gui
{
  private int networkId;
  private int x, y;

  public GuiAutoChestHud()
  {
  }

  public void setData(int x, int y, int networkId)
  {
    this.x = x;
    this.y = y;
    this.networkId = networkId;
  }

  public void draw(Minecraft mc, AutoChestTileEntity te)
  {
    drawCenteredString(mc.fontRenderer, "Network: " + networkId, x, y, Integer.parseInt("FFAA00", 16));

    int itemX = 0;
    int itemY = 1;
    
    for (int fIndex = 0; fIndex < AutoChestTileEntity.AUTOCHEST_FILTER_SIZE; fIndex++)
    {
      ItemStack stack = te.filter.getStackInSlot(fIndex);
      if (StackUtil.isValid(stack))
      {
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 
            x - 90 + 20 * itemX, 
            y + 20 * itemY);
        ++itemX;
        if (itemX > 8)
        {
          itemX = 0;
          ++itemY;
        }
      } else
        break;
    }
  }
}