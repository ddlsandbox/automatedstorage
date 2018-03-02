package automatedstorage.item;

import automatedstorage.block.chest.AutoChestTileEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemConfigurator extends ItemBase
{

  private static enum Mode { MODE_CLONE_NETWORK, MODE_SET_NETWORK };
  
  public ItemConfigurator(String name)
  {
    super(name);
    
    maxStackSize = 1;
  }

  @Override
  public ItemConfigurator setCreativeTab(CreativeTabs tab)
  {
    super.setCreativeTab(tab);
    return this;
  }

  @Override
  public int getItemStackLimit(ItemStack p_getItemStackLimit_1_)
  {
    return 1;
  }
  
  @Override
  public int getItemStackLimit()
  {
    return 1;
  }
  
  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world,
      BlockPos pos, EnumHand hand, EnumFacing side, float par8, float par9,
      float par10)
  {
    ItemStack stack = player.getHeldItem(hand);
    
    if (player.isSneaking())
    {
      /* switch mode */
      switch (getMode(stack))
      {
      case MODE_CLONE_NETWORK:
        setMode(stack, Mode.MODE_SET_NETWORK);
        break;
      case MODE_SET_NETWORK:
        setMode(stack, Mode.MODE_CLONE_NETWORK);
        break;
      }      
      
      if (world.isRemote)
      {
        player.sendMessage(new TextComponentString("Switch configurator mode to " + getMode(stack)));
      }
    }
    else
    {
      TileEntity tile = world.getTileEntity(pos);
      
      if (tile instanceof AutoChestTileEntity)
      {
        AutoChestTileEntity autochest = (AutoChestTileEntity) tile;
        
        switch (getMode(stack))
        {
        case MODE_CLONE_NETWORK:
          setStoredNetwork(stack, autochest.getNetworkId());
          if (world.isRemote)
          {
            player.sendMessage(new TextComponentString("Stored network " + getStoredNetwork(stack)));
          }
          break;
        case MODE_SET_NETWORK:
          autochest.setNetworkId(getStoredNetwork(stack));
          if (world.isRemote)
          {
            player.sendMessage(new TextComponentString("Network set to " + getStoredNetwork(stack)));
          }
          break;
        }  
        
        return EnumActionResult.SUCCESS;
      }
    }
    
    return super.onItemUse(player, world, pos, hand, side, par8, par9, par10);
  }

  public Mode getMode(ItemStack itemStack)
  {
    return Mode.values()[ItemUtil.getInt(itemStack, "ConfiguratorMode")];
  }
  
  public void setMode(ItemStack itemStack, Mode mode)
  {
    ItemUtil.setInt(itemStack, "ConfiguratorMode", mode.ordinal());
  }
  
  public int getStoredNetwork(ItemStack itemStack)
  {
    return ItemUtil.getInt(itemStack, "StoredNetwork");
  }
  
  public void setStoredNetwork(ItemStack itemStack, int networkId)
  {
    ItemUtil.setInt(itemStack, "StoredNetwork", networkId);
  }
  
  @Override
  public EnumRarity getRarity(ItemStack stack)
  {
    return EnumRarity.RARE;
  }

}
