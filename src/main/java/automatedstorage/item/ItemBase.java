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
package automatedstorage.item;

import automatedstorage.AutomatedStorage;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBase extends Item implements ItemModelProvider
{

  protected String name;

  public ItemBase(String name)
  {
    this.name = name;
    setUnlocalizedName(name);
    setRegistryName(name);
  }

  @Override
  public void registerItemModel(Item item)
  {
    AutomatedStorage.proxy.registerItemRenderer(this, 0, name);
  }

  @Override
  public ItemBase setCreativeTab(CreativeTabs tab)
  {
    super.setCreativeTab(tab);
    return this;
  }

  @SideOnly(Side.CLIENT)
  public void initModel()
  {
    ModelLoader.setCustomModelResourceLocation(this, 0,
        new ModelResourceLocation(getRegistryName(), "inventory"));
  }
}