// START new code
package com.firstdwarf.underneath.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

import java.util.logging.LogManager;

public class BlockWrapper {

    //added blocks
    private static Block testBlock;
    private static Block anotherTestBlock;
    private static Porter porter;

    public static void makeBlock(RegistryEvent.Register<Block> blockRegistryEvent) {
        Block.Properties rockBlockProps = Block.Properties.create(Material.ROCK); //set up properties for block

        testBlock = new Block(rockBlockProps); //initialize block
        testBlock.setRegistryName(new ResourceLocation("underneath:grass")); //set registry name (texture!) - resource location name must be lowercase
        blockRegistryEvent.getRegistry().register(testBlock); //register block

        anotherTestBlock = new Block(rockBlockProps);
        anotherTestBlock.setRegistryName(new ResourceLocation("underneath:sun"));
        blockRegistryEvent.getRegistry().register(anotherTestBlock);
        
        porter = new Porter(rockBlockProps);
        porter.setRegistryName(new ResourceLocation("underneath:porter"));
        blockRegistryEvent.getRegistry().register(porter);
    }

    public static void makeBlockItem(RegistryEvent.Register<Item> blockItemRegistryEvent) {
        //initialize block item

        Item.Properties brewingBlockItemProps = new Item.Properties().group(ItemGroup.BREWING);

        Item testBlockItem = new BlockItem(testBlock, brewingBlockItemProps).setRegistryName(testBlock.getRegistryName());
        blockItemRegistryEvent.getRegistry().register(testBlockItem); //register block item

        Item anotherTestBlockItem = new BlockItem(anotherTestBlock, brewingBlockItemProps).setRegistryName(anotherTestBlock.getRegistryName());
        blockItemRegistryEvent.getRegistry().register(anotherTestBlockItem);
        
        Item porterItem = new BlockItem(porter, brewingBlockItemProps).setRegistryName(porter.getRegistryName());
        blockItemRegistryEvent.getRegistry().register(porterItem);

    }
}
// END new code
