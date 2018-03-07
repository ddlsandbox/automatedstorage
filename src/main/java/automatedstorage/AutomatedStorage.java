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
package automatedstorage;

import automatedstorage.block.ModBlocks;
import automatedstorage.command.ViewNetworksCommand;
import automatedstorage.command.ViewRegistryCommand;
import automatedstorage.gui.ModGuiHandler;
import automatedstorage.item.ModItems;
import automatedstorage.proxy.CommonProxy;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod.EventBusSubscriber
@Mod(modid = AutomatedStorage.modId, name = AutomatedStorage.name, version = AutomatedStorage.version, acceptedMinecraftVersions = "[1.12.2]")
public class AutomatedStorage {

	  @SidedProxy(serverSide = "automatedstorage.proxy.CommonProxy", clientSide = "automatedstorage.proxy.ClientProxy")
	  public static CommonProxy                 proxy;
	  public static final CreativeTab creativeTab = new CreativeTab();
	  
	  public static final String modId   = "automatedstorage";
	  public static final String name    = "Automated Storage";
	  public static final String version = "0.1.0";

	  public static SimpleNetworkWrapper network;
	  
	  @Mod.Instance(modId)
	  public static AutomatedStorage instance;
	  
	  @Mod.EventHandler
	  public void preInit(FMLPreInitializationEvent event)
	  {
	    network = NetworkRegistry.INSTANCE.newSimpleChannel(AutomatedStorage.modId);
	    
	    ModBlocks.init();
	    ModItems.init();

	    NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());
	    
	    proxy.preInit(event);
	  }

	  @Mod.EventHandler
	  public void init(FMLInitializationEvent event)
	  {
	    proxy.init(event);
	  }

	  @Mod.EventHandler
	  public void postInit(FMLPostInitializationEvent event)
	  {
	    proxy.postInit(event);
	  }

	  /* events */
	  
	  @SubscribeEvent
	  public static void registerModels(ModelRegistryEvent event) {
	    proxy.loadModels();
	  }
	  
	  @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new ViewRegistryCommand());
        event.registerServerCommand(new ViewNetworksCommand());
    }
}
