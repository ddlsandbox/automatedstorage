package automatedstorage;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs {

	public CreativeTab() {
			super(AutomatedStorage.modId);
		}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(Items.CHEST_MINECART);
	}

}
