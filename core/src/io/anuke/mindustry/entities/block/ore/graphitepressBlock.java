package io.anuke.mindustry.entities.block.ore;

import io.anuke.mindustry.content.*;
import io.anuke.mindustry.type.*;
import io.anuke.mindustry.world.blocks.*;

public class graphitepressBlock extends OreBlock {
	public graphitepressBlock(String name){
		super(name);
        oreScrap = new OreBlock(Items.scrap);
        oreCoal = new OreBlock(Items.coal);
        oreTitanium = new OreBlock(Items.titanium);
        oreThorium = new OreBlock(Items.thorium);

        //endregion
        //region crafting

        graphitePress = new GenericCrafter("graphite-press"){{
            requirements(Category.crafting, ItemStack.with(Items.copper, 150, Items.lead, 60));

            craftEffect = Fx.pulverizeMedium;
            outputItem = new ItemStack(Items.graphite, 1);
            craftTime = 90f;
            size = 2;
            hasItems = true;

            consumes.item(Items.coal, 2);
        
	}
}