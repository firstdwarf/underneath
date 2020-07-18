package com.example.examplemod;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;

public class ItemWrapper {
    //new Items
    private static Item testItem;

    public static void makeItem(RegistryEvent.Register<Item> itemRegistryEvent) {
        Item.Properties brewingItemProps = new Item.Properties().group(ItemGroup.BREWING);
        testItem = new Item(brewingItemProps).setRegistryName("examplemod:cat");
        itemRegistryEvent.getRegistry().register(testItem);
    }
}
