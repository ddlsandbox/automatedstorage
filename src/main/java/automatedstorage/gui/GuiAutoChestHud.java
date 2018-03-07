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
package automatedstorage.gui;

import automatedstorage.item.StackUtil;
import automatedstorage.tileentity.TileEntityAutoChest;
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

  public void draw(Minecraft mc)
  {
    drawCenteredString(mc.fontRenderer, "Network: " + networkId, x, y, Integer.parseInt("FFAA00", 16));
  }
  
  public void draw(Minecraft mc, TileEntityAutoChest te)
  {
    draw(mc);

    int itemX = 0;
    int itemY = 1;
    
    for (int fIndex = 0; fIndex < TileEntityAutoChest.AUTOCHEST_FILTER_SIZE; fIndex++)
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