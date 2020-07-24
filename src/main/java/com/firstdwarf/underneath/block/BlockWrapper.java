// START new code
package com.firstdwarf.underneath.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

import java.util.logging.LogManager;

import com.firstdwarf.underneath.Underneath;

public class BlockWrapper {

    //added blocks
    private static Block testBlock;
    private static Block anotherTestBlock;
    private static Block deepStone;
    private static Porter porter;
    private static final ItemGroup DEEP_BLOCKS = (new ItemGroup("deep_blocks") {
    	@OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
           return new ItemStack(deepStone);
        }
    });
    
    public static Material deepMat;

    public static void makeBlock(RegistryEvent.Register<Block> e) {
    	
    	deepMat = new Material(MaterialColor.BLACK, false, true, true, true, false, false, false, PushReaction.BLOCK);
    	
    	Block.Properties deepProps = Block.Properties.create(deepMat);
    	
        Block.Properties rockBlockProps = Block.Properties.create(Material.ROCK); //set up properties for block

        deepStone = new Block(deepProps);
        deepStone.setRegistryName(new ResourceLocation("underneath:deep_stone"));
        e.getRegistry().register(deepStone);
        
        testBlock = new Block(rockBlockProps); //initialize block
        testBlock.setRegistryName(new ResourceLocation("underneath:grass")); //set registry name (texture!) - resource location name must be lowercase
        e.getRegistry().register(testBlock); //register block

        anotherTestBlock = new Block(rockBlockProps);
        anotherTestBlock.setRegistryName(new ResourceLocation("underneath:sun"));
        e.getRegistry().register(anotherTestBlock);
        
        porter = new Porter(rockBlockProps);
        porter.setRegistryName(new ResourceLocation("underneath:porter"));
        e.getRegistry().register(porter);
    }

    public static void makeBlockItem(RegistryEvent.Register<Item> e) {
        //initialize block item

        Item.Properties deepBlocks = new Item.Properties().group(DEEP_BLOCKS);
        
        Item deepStoneItem = new BlockItem(deepStone, deepBlocks).setRegistryName(deepStone.getRegistryName());
        e.getRegistry().register(deepStoneItem);

        Item testBlockItem = new BlockItem(testBlock, deepBlocks).setRegistryName(testBlock.getRegistryName());
        e.getRegistry().register(testBlockItem); //register block item

        Item anotherTestBlockItem = new BlockItem(anotherTestBlock, deepBlocks).setRegistryName(anotherTestBlock.getRegistryName());
        e.getRegistry().register(anotherTestBlockItem);
        
        Item porterItem = new BlockItem(porter, deepBlocks).setRegistryName(porter.getRegistryName());
        e.getRegistry().register(porterItem);

    }
}
// END new code
