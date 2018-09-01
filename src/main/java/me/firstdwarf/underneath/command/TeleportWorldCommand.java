package me.firstdwarf.underneath.command;

import me.firstdwarf.underneath.world.CustomTeleporter;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class TeleportWorldCommand extends CommandBase {

	//TODO: Spawns player barely inside block in overworld?
    /**
     * This command is needed so that way we can get to the Underneath world
     */

    @Override
    public String getName() {
        return "tpw";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tpw <world id>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP) || args.length < 1) {
            return;
        }

        int dimensionId;

        try {
             dimensionId = Integer.valueOf(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + args[0] + " is not a valid dimension ID"));
            return;
        }

        WorldServer world = server.getWorld(dimensionId);
        BlockPos target = world.getSpawnPoint();
        world.getMinecraftServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) sender, dimensionId, new CustomTeleporter(world, target.getX(), target.getY(), target.getZ()));
    }
}
