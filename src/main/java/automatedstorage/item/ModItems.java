package automatedstorage.item;

import automatedstorage.AutomatedStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems
{

  public static ItemConfigurator configurator;
  
  public static void init() {
    configurator = new ItemConfigurator("configurator").setCreativeTab(AutomatedStorage.creativeTab);
  }
  
  @SideOnly(Side.CLIENT)
  public static void initModels() {
    configurator.initModel();
  }
}
