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
package automatedstorage.block;

import automatedstorage.AutomatedStorage;
import automatedstorage.item.ItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBase extends Block implements ItemModelProvider
{

  protected String name;

  public BlockBase(Material materialIn, String name)
  {
    this(materialIn, name, "pickaxe", 0, 1.5F, 10.0F, SoundType.STONE);
  }

  public BlockBase(Material materialIn, String name, String harvestTool, int harvestLevel, float hardness,
      float resistance, SoundType soundType)
  {
    super(materialIn);
    this.name = name;

    setUnlocalizedName(AutomatedStorage.modId + "." + name);
    setRegistryName(name);

    this.setHarvestLevel(harvestTool, harvestLevel);
    this.setHardness(hardness);
    this.setResistance(resistance);
    this.setSoundType(soundType);
  }

  @SideOnly(Side.CLIENT)
  public void initModel()
  {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
        new ModelResourceLocation(getRegistryName(), "inventory"));
  }
  
  public ItemBlock getItemBlock(){
    return new ItemBlock(this);
  }
  
  @Override
  public void registerItemModel(Item itemBlock)
  {
    AutomatedStorage.proxy.registerItemRenderer(itemBlock, 0, name);
  }

  @Override
  public BlockBase setCreativeTab(CreativeTabs tab)
  {
    super.setCreativeTab(tab);
    return this;
  }

}
