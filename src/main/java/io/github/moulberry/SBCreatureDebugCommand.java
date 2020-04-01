package io.github.moulberry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class SBCreatureDebugCommand extends CommandBase {

    public String getCommandName() {
        return "sbcreaturedebug";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/sbcreaturedebug";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        SBCustomMobTex.INSTANCE.toggleDebugMode();
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }
}
