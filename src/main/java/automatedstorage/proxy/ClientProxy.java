package automatedstorage.proxy;

import automatedstorage.AutomatedStorage;
import automatedstorage.block.ModBlocks;
import automatedstorage.block.chest.AutoChestTileEntity;
import automatedstorage.gui.GuiAutoChestHud;
import automatedstorage.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{

  private static GuiAutoChestHud guiAutoChestHud;
  
  @Override
  public void preInit(FMLPreInitializationEvent event)
  {
    super.preInit(event);
    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void init(FMLInitializationEvent event)
  {
  }

  @Override
  public void postInit(FMLPostInitializationEvent event)
  {
  }

  public void registerItemRenderer(Item item, int meta, String id)
  {
    ModelLoader.setCustomModelResourceLocation(item, meta,
        new ModelResourceLocation(AutomatedStorage.modId + ":" + id, "inventory"));
  }

  @Override
  public String localize(String unlocalized, Object... args)
  {
    // return I18n.format(unlocalized, args);
    return I18n.translateToLocalFormatted(unlocalized, args);
  }

  @Override
  public void loadModels()
  {
    ModBlocks.initModels();
    ModItems.initModels();
  }

  @Override
  public void playSound(SoundEvent sound, BlockPos pos, float pitch)
  {
    Minecraft.getMinecraft().getSoundHandler()
        .playSound(new PositionedSoundRecord(sound, SoundCategory.AMBIENT, 0.5f, pitch, pos));
  }
  
  @SubscribeEvent
  public void onGameOverlay(RenderGameOverlayEvent.Post event)
  {
    if (event.getType() == RenderGameOverlayEvent.ElementType.ALL && Minecraft.getMinecraft().currentScreen == null)
    {
      Minecraft minecraft = Minecraft.getMinecraft();
      RayTraceResult posHit = minecraft.objectMouseOver;

      if (posHit != null && posHit.getBlockPos() != null)
      {
        EntityPlayer player = minecraft.player;
        if (player.getHeldItemMainhand().getItem() == ModItems.configurator)
        {
          TileEntity tileHit = minecraft.world.getTileEntity(posHit.getBlockPos());
          
          if (tileHit instanceof AutoChestTileEntity)
          {
            if (guiAutoChestHud == null)
            {
              guiAutoChestHud = new GuiAutoChestHud();
            }
            
            AutoChestTileEntity chest = (AutoChestTileEntity) tileHit;
            
            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);
  
            guiAutoChestHud.setData(
                event.getResolution().getScaledWidth()/2,
                event.getResolution().getScaledHeight()/2,
                chest.getNetworkId());
            guiAutoChestHud.draw(minecraft, chest);
  
            GlStateManager.popMatrix();
          }
        }
      }
    }
  }
}