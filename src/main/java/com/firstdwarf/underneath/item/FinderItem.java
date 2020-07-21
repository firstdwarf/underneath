package com.firstdwarf.underneath.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class FinderItem extends Item {

    public FinderItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        BlockPos p = playerIn.getPosition();
        int offset = 1;
        String bm = worldIn.getBiome(p).getRegistryName().toString();
        BlockPos target = p;
        while(!bm.equals("underneath:test_biome") && offset < 100) {
           for(int i = -offset; i <= offset; i++) {
               target = new BlockPos(p.getX() + 16*offset, p.getY(), p.getZ() + 16*i);
               bm = worldIn.getBiome(target).getRegistryName().toString();
               System.out.println(target.toString() + " " + bm);
               if(bm.equals("underneath:test_biome")) break;

               target = new BlockPos(p.getX() - 16*offset, p.getY(), p.getZ() + 16*i);
               bm = worldIn.getBiome(target).getRegistryName().toString();
               System.out.println(target.toString() + " " + bm);
               if(bm.equals("underneath:test_biome")) break;
           }
           if(bm.equals("underneath:test_biome")) break;
           for(int i = -offset + 1; i <= offset - 1; i++) {
               target = new BlockPos(p.getX() + 16*i, p.getY(), p.getZ() + 16*offset);
               bm = worldIn.getBiome(target).getRegistryName().toString();
               System.out.println(target.toString() + " " + bm);
               if(bm.equals("underneath:test_biome")) break;

               target = new BlockPos(p.getX() + 16*i, p.getY(), p.getZ() - 16*offset);
               bm = worldIn.getBiome(target).getRegistryName().toString();
               System.out.println(target.toString() + " " + bm);
               if(bm.equals("underneath:test_biome")) break;
           }
           offset++;
        }
        String msg = (bm.equals("underneath:test_biome")) ? "Found at " + target : "Could not find biome";
        playerIn.sendMessage(new StringTextComponent(msg));
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }
}
