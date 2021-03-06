package zaneextras.blocks.ore;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import zaneextras.items.ItemList;
import zaneextras.lib.ModInfo;
import zaneextras.lib.ZaneTabs;

public class FoolStariaOreBlock extends Block {
	
	public Item droppedItem = ItemList.foolStaria;
	
	public FoolStariaOreBlock() {
		super(Material.rock);
		this.setCreativeTab(ZaneTabs.zTab);
		this.setStepSound(soundTypeMetal);
		this.setBlockName(ModInfo.MODID + "_foolstaria");
		this.setBlockTextureName(ModInfo.MODID + ":stariaore");
		this.setHardness(1.0F);
		this.setResistance(1.0F);
		this.setHarvestLevel("pickaxe", 1);
	}
	
	private Random rand = new Random();
	
	@Override
	public int quantityDropped(Random p_149745_1_) {
		if (p_149745_1_.nextInt(10) > 5) {
			return 2;
		} else {
			return 1;
		}
	}
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_,
			int p_149650_3_) {
		return this.droppedItem;
	}
	
	@Override
	public int getExpDrop(IBlockAccess p_149690_1_, int p_149690_5_,
			int p_149690_7_) {
		if (this.getItemDropped(p_149690_5_, rand, p_149690_7_) != Item
				.getItemFromBlock(this)) {
			int j1 = 0;
			j1 = MathHelper.getRandomIntegerInRange(rand, 4, 8);
			
			return j1;
		}
		return 0;
	}
}
