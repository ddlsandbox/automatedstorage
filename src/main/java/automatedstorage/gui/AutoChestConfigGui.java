package automatedstorage.gui;

import java.awt.Color;

import automatedstorage.AutomatedStorage;
import automatedstorage.block.ModBlocks;
import automatedstorage.block.chest.AutoChestConfigContainer;
import automatedstorage.block.chest.AutoChestTileEntity;
import automatedstorage.network.PacketUpdateNetwork;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

public class AutoChestConfigGui extends GuiContainer
{
  public static final int WIDTH = 176;
  public static final int HEIGHT = 126 + 86;

  private static final int BUTTON_ADD_X = 150;
  private static final int BUTTON_ADD_Y = 6;
  private static final int BUTTON_SUB_X = 100;
  private static final int BUTTON_SUB_Y = 6;
  
  private static final ResourceLocation gui_main = new ResourceLocation(AutomatedStorage.modId,
      "textures/gui/autochest_config.png");
  private static final ResourceLocation gui_inv = new ResourceLocation(AutomatedStorage.modId,
      "textures/gui/inventory.png");
  
  private AutoChestTileEntity tileEntity;
  AutoChestConfigContainer container;
  private InventoryPlayer playerInv;
  
  public AutoChestConfigGui(AutoChestTileEntity tileEntity, AutoChestConfigContainer container, InventoryPlayer playerInv)
  {
    super(container);

    xSize = WIDTH;
    ySize = HEIGHT;
    
    this.tileEntity = tileEntity;
    this.playerInv = playerInv;
    this.container = container;
  }
  
  @Override
  public void initGui()
  {
    super.initGui();
    buttonList.add(
        new GuiButton(0, 
            guiLeft + BUTTON_ADD_X, 
            guiTop + BUTTON_ADD_Y, 
            18, 
            18, 
            ">"));
    buttonList.add(
        new GuiButton(1, 
            guiLeft + BUTTON_SUB_X, 
            guiTop + BUTTON_SUB_Y, 
            18, 
            18, 
            "<"));
  }

  @Override
  protected void actionPerformed(GuiButton button)
  {
    try
    {
      int newNetwork;
      switch(button.id)
      {
      case 0:
        newNetwork = tileEntity.getNetworkId() + 1;
        break;
      case 1:
        newNetwork = tileEntity.getNetworkId() - 1;
        break;
      default:
        newNetwork = tileEntity.getNetworkId();
      }
      container.updateNetwork(newNetwork);
      
      AutomatedStorage.network.sendToServer(new PacketUpdateNetwork(tileEntity));
    } catch (Exception e)
    {
      playerInv.player.sendMessage(new TextComponentString("GUI Exception: " + e.getMessage()));
    }
    return;
  }
  
  @Override
  public void updateScreen()
  {
    super.updateScreen();
  }

  @Override
  public void drawGuiContainerForegroundLayer(int x, int y)
  {
    final String name = AutomatedStorage.proxy.localize(ModBlocks.autoChest.getUnlocalizedName() + ".name") + " Config";
    final int LABEL_XPOS = (xSize) / 2 - fontRenderer.getStringWidth(name) / 2;
    final int LABEL_YPOS = -10;
    fontRenderer.drawString(name, LABEL_XPOS, LABEL_YPOS, Color.cyan.getRGB());
    
    final String filterName = "Filter";
    final int FILTER_YPOS = 5;
    final int FILTER_XPOS = (xSize) / 2 - fontRenderer.getStringWidth(filterName) / 2;
    fontRenderer.drawString(filterName, FILTER_XPOS, FILTER_YPOS, Color.white.getRGB());
    
    fontRenderer.drawString(String.valueOf(tileEntity.getNetworkId()), 136, 6, Color.cyan.getRGB());
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
