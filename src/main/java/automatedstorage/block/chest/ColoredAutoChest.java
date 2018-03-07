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
package automatedstorage.block.chest;

import automatedstorage.AutomatedStorage;
import automatedstorage.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ColoredAutoChest extends AutoChest
{
  public static final AutoChestColors[] ALL_COLORS = AutoChestColors.values();
  public static final PropertyEnum<AutoChestColors> COLOR = PropertyEnum.create("color", AutoChestColors.class);
  
  public ColoredAutoChest(String name)
  {
    super(name);
  }
  
  /* COLOR STUFF */
  
  @Override
  public int damageDropped(IBlockState state)
  {
    return this.getMetaFromState(state);
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
  {
    for(int i = 0; i < ALL_COLORS.length; i++){
      list.add(new ItemStack(this, 1, i));
    }
  }
  
  @Override
  public ItemBlock getItemBlock(){
      return new ItemBlockCustom(this);
  }
  
  @Override
  public void registerItemModel(Item itemBlock)
  {
    for (int i = 0; i < ALL_COLORS.length; i++)
    {
      AutomatedStorage.proxy.registerItemRenderer(itemBlock, i, name + "_" + ALL_COLORS[i].regName);
    }
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void initModel()
  {
    for (int i = 0; i < ColoredAutoChest.ALL_COLORS.length; i++)
    {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i,
        new ModelResourceLocation(AutomatedStorage.modId + ":" + ColoredAutoChest.ALL_COLORS[i].regName + "_autochest", "inventory"));
    }
  }
  
  @Override
  public MapColor getMapColor(IBlockState state, IBlockAccess access, BlockPos pos)
  {
    return state.getMapColor(access, pos);
  }
  
  @Override
  public IBlockState getStateFromMeta(int meta)
  {
    return this.getDefaultState().withProperty(COLOR, AutoChestColors.values()[meta]);
  }
  
  @Override
  public int getMetaFromState(IBlockState state)
  {
    return state.getValue(COLOR).ordinal();
  }
  
  @Override
  protected BlockStateContainer createBlockState()
  {
    return new BlockStateContainer(this, COLOR);
  }
  
  public static class ItemBlockCustom extends ItemBlock
  {

    public ItemBlockCustom(Block block){
        super(block);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage){
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        return ModBlocks.autoChest.getUnlocalizedName()+"_"+ALL_COLORS[stack.getItemDamage()].regName;
    }
}
}