package com.firstdwarf.underneath.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

public class ItemWrapper {
    //new Items
    private static Item testItem;

    public static void makeItem(RegistryEvent.Register<Item> itemRegistryEvent) {
        Item.Properties brewingItemProps = new Item.Properties().group(ItemGroup.BREWING);
        testItem = new FinderItem(brewingItemProps).setRegistryName(new ResourceLocation("underneath:cat"));
        itemRegistryEvent.getRegistry().register(testItem);
    }
}
