// START new code
package com.example.examplemod;

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

    public static void makeBlock(RegistryEvent.Register<Block> blockRegistryEvent) {
        Block.Properties rockBlockProps = Block.Properties.create(Material.ROCK); //set up properties for block

        testBlock = new Block(rockBlockProps); //initialize block
        testBlock.setRegistryName(new ResourceLocation("examplemod:grass")); //set registry name (texture!) - resource location name must be lowercase
        blockRegistryEvent.getRegistry().register(testBlock); //register block

        anotherTestBlock = new Block(rockBlockProps);
        anotherTestBlock.setRegistryName(new ResourceLocation("examplemod:sun"));
        blockRegistryEvent.getRegistry().register(anotherTestBlock);
    }

    public static void makeBlockItem(RegistryEvent.Register<Item> blockItemRegistryEvent) {
        //initialize block item

        Item.Properties brewingBlockItemProps = new Item.Properties().group(ItemGroup.BREWING);

        Item testBlockItem = (BlockItem) new BlockItem(testBlock, brewingBlockItemProps).setRegistryName(testBlock.getRegistryName());
        blockItemRegistryEvent.getRegistry().register(testBlockItem); //register block item

        Item anotherTestBlockItem = (BlockItem) new BlockItem(anotherTestBlock, brewingBlockItemProps).setRegistryName(anotherTestBlock.getRegistryName());
        blockItemRegistryEvent.getRegistry().register(anotherTestBlockItem);

    }
}
// END new code
