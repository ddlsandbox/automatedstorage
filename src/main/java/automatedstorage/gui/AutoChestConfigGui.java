package automatedstorage.gui;

import java.awt.Color;

import automatedstorage.AutomatedStorage;
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

  private static final int BUTTON_ADD_X = 154;
  private static final int BUTTON_ADD_Y = 5;
  private static final int BUTTON_SUB_X = 117;
  private static final int BUTTON_SUB_Y = 5;
  
  private static final ResourceLocation gui_main = new ResourceLocation(AutomatedStorage.modId,
      "textures/gui/autochest_config.png");
  private static final ResourceLocation gui_inv = new ResourceLocation(AutomatedStorage.modId,
      "textures/gui/inventory.png");
  
  private AutoChestTileEntity tileEntity;
  AutoChestConfigContainer container;
  private InventoryPlayer playerInv;
  
  private String guiName;
  
  public AutoChestConfigGui(AutoChestTileEntity tileEntity, AutoChestConfigContainer container, InventoryPlayer playerInv)
  {
    super(container);

    xSize = WIDTH;
    ySize = HEIGHT;
    
    guiName = tileEntity.getBlockType().getLocalizedName() + " Config";
    
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
    final int LABEL_XPOS = (xSize) / 2 - fontRenderer.getStringWidth(guiName) / 2;
    final int LABEL_YPOS = -10;
    fontRenderer.drawString(guiName, LABEL_XPOS, LABEL_YPOS, Color.cyan.getRGB());
    
    final String filterName = "Network:";
    final int FILTER_YPOS = 10;
    final int FILTER_XPOS = BUTTON_SUB_X - fontRenderer.getStringWidth(filterName) - 4;
    fontRenderer.drawString(filterName, FILTER_XPOS, FILTER_YPOS, Color.black.getRGB());
    
    fontRenderer.drawString("Item Filter", 9, 16, Color.black.getRGB());
    fontRenderer.drawString("Meta Filter", 9, 88, Color.black.getRGB());
    
    String netIdStr = String.valueOf(tileEntity.getNetworkId());
    int strX = 136 + (18 - fontRenderer.getStringWidth(netIdStr))/2;
    fontRenderer.drawString(netIdStr, strX, 10, Color.cyan.getRGB());
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
