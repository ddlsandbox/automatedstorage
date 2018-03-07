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
package automatedstorage.proxy;

import automatedstorage.AutomatedStorage;
import automatedstorage.block.ModBlocks;
import automatedstorage.block.chest.AutoChest;
import automatedstorage.block.chest.ColoredAutoChest;
import automatedstorage.item.ModItems;
import automatedstorage.network.PacketUpdateNetwork;
import automatedstorage.tileentity.TileEntityAutoChest;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
    AutomatedStorage.network.registerMessage(new PacketUpdateNetwork.Handler(), PacketUpdateNetwork.class, 
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
    event.getRegistry().register(new ColoredAutoChest("autochest").setCreativeTab(AutomatedStorage.creativeTab));
    event.getRegistry().register(new AutoChest("autochest_source").setCreativeTab(AutomatedStorage.creativeTab));
    event.getRegistry().register(new AutoChest("autochest_sink").setCreativeTab(AutomatedStorage.creativeTab));
    GameRegistry.registerTileEntity(TileEntityAutoChest.class, AutomatedStorage.modId + "_autochest");
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> event)
  {
    event.getRegistry().register(ModBlocks.autoChest.getItemBlock().setRegistryName(ModBlocks.autoChest.getRegistryName()));
    event.getRegistry().register(ModBlocks.autoChestSource.getItemBlock().setRegistryName(ModBlocks.autoChestSource.getRegistryName()));
    event.getRegistry().register(ModBlocks.autoChestSink.getItemBlock().setRegistryName(ModBlocks.autoChestSink.getRegistryName()));
    
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
