package automatedstorage.gui;

import java.awt.Color;

import automatedstorage.AutomatedStorage;
import automatedstorage.block.chest.AutoChestContainer;
import automatedstorage.block.chest.AutoChestTileEntity;
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

  public AutoChestGui(AutoChestTileEntity tileEntity, AutoChestContainer container)
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
