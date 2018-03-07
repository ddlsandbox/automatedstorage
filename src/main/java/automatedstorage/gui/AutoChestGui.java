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

import java.awt.Color;

import automatedstorage.AutomatedStorage;
import automatedstorage.container.ContainerAutoChest;
import automatedstorage.tileentity.TileEntityAutoChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class AutoChestGui extends GuiContainer
{
  public static final int WIDTH = 176;
  public static final int HEIGHT = 126 + 86;
  
  private String guiName;

  private static final ResourceLocation gui_main = new ResourceLocation(AutomatedStorage.modId,
      "textures/gui/autochest.png");
  private static final ResourceLocation gui_inv = new ResourceLocation(AutomatedStorage.modId,
      "textures/gui/inventory.png");

  public AutoChestGui(TileEntityAutoChest tileEntity, ContainerAutoChest container)
  {
    super(container);

    guiName = tileEntity.getBlockType().getLocalizedName();
    xSize = WIDTH;
    ySize = HEIGHT;
  }

  @Override
  public void updateScreen()
  {
    super.updateScreen();
  }

  @Override
  public void drawGuiContainerForegroundLayer(int x, int y)
  {
    final int LABEL_XPOS = (xSize) / 2 - fontRenderer.getStringWidth(guiName) / 2;
    final int LABEL_YPOS = -10;
    fontRenderer.drawString(guiName, LABEL_XPOS, LABEL_YPOS, Color.cyan.getRGB());
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
  {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    this.mc.getTextureManager().bindTexture(gui_main);
    this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 176, 126);

    this.mc.getTextureManager().bindTexture(gui_inv);
    this.drawTexturedModalRect(this.guiLeft, this.guiTop + 126, 0, 0, 176, 86);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks)
  {
    this.drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }
}
