package automatedstorage.block;


import automatedstorage.block.chest.AutoChest;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks
{

  @GameRegistry.ObjectHolder("automatedstorage:autochest")
  public static AutoChest autoChest;
  
  @GameRegistry.ObjectHolder("automatedstorage:autochest_source")
  public static AutoChest autoChestSource;
  
  public static void init() {
    
  }
  
  @SideOnly(Side.CLIENT)
  public static void initModels() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(autoChest), 0, new ModelResourceLocation(autoChest.getRegistryName(), "inventory"));
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(autoChestSource), 0, new ModelResourceLocation(autoChestSource.getRegistryName(), "inventory"));
  }
}
