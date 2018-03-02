package automatedstorage.proxy;


import automatedstorage.AutomatedStorage;
import automatedstorage.block.ModBlocks;
import automatedstorage.block.chest.AutoChest;
import automatedstorage.block.chest.AutoChestTileEntity;
import automatedstorage.item.ModItems;
import automatedstorage.network.PacketRequestUpdateNetwork;
import automatedstorage.network.PacketServerToClient;
import automatedstorage.network.PacketUpdateNetwork;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber
public class CommonProxy
{
  public void preInit(FMLPreInitializationEvent event)
  {
    int messageId = 0;
    AutomatedStorage.network.registerMessage(new PacketServerToClient.Handler(), PacketServerToClient.class, 
        messageId++, Side.CLIENT);
    
    AutomatedStorage.network.registerMessage(new PacketUpdateNetwork.Handler(), PacketUpdateNetwork.class, 
        messageId++, Side.SERVER);
    AutomatedStorage.network.registerMessage(new PacketRequestUpdateNetwork.Handler(), PacketRequestUpdateNetwork.class, 
        messageId++, Side.SERVER);
  }

  public void init(FMLInitializationEvent event)
  {
  }

  public void postInit(FMLPostInitializationEvent event)
  {
  }

  public String localize(String unlocalized, Object... args)
  {
    return I18n.translateToLocalFormatted(unlocalized, args);
  }

  @SubscribeEvent
  public static void registerBlocks(RegistryEvent.Register<Block> event)
  {
    event.getRegistry().register(new AutoChest("autochest").setCreativeTab(AutomatedStorage.creativeTab));
    event.getRegistry().register(new AutoChest("autochest_source").setCreativeTab(AutomatedStorage.creativeTab));
    event.getRegistry().register(new AutoChest("autochest_sink").setCreativeTab(AutomatedStorage.creativeTab));
    GameRegistry.registerTileEntity(AutoChestTileEntity.class, AutomatedStorage.modId + "_autochest");
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event)
  {
    event.getRegistry().register(new ItemBlock(ModBlocks.autoChest).setRegistryName(ModBlocks.autoChest.getRegistryName()));
    event.getRegistry().register(new ItemBlock(ModBlocks.autoChestSource).setRegistryName(ModBlocks.autoChestSource.getRegistryName()));
    event.getRegistry().register(new ItemBlock(ModBlocks.autoChestSink).setRegistryName(ModBlocks.autoChestSink.getRegistryName()));
    
    event.getRegistry().register(ModItems.configurator);
  }

  public void registerItemRenderer(Item item, int meta, String id)
  {
    /* do nothing */
  }

  public void registerRenderers()
  {
    /* do nothing */
  }

  public void loadModels()
  {
    /* do nothing */
  }

  public void playSound(SoundEvent sound, BlockPos pos, float pitch)
  {
    /* do nothing */
  }
}
