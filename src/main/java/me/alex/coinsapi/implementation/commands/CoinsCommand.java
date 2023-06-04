package me.alex.coinsapi.implementation.commands;

import dev.hypera.chameleon.command.Command;
import dev.hypera.chameleon.command.annotations.CommandHandler;
import dev.hypera.chameleon.command.annotations.Permission;
import dev.hypera.chameleon.command.context.Context;
import org.jetbrains.annotations.NotNull;

@Permission("coinsapi.command.coins")
@CommandHandler("coins|coin")
public class CoinsCommand extends Command {

    @Override
    public void execute(@NotNull Context context) {

    }
}
