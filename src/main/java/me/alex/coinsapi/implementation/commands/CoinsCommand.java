package me.alex.coinsapi.implementation.commands;

import dev.hypera.chameleon.command.Command;
import dev.hypera.chameleon.command.annotations.CommandHandler;
import dev.hypera.chameleon.command.context.Context;
import dev.hypera.chameleon.user.User;
import me.alex.coinsapi.api.CoinUser;
import me.alex.coinsapi.api.CoinUserDAO;
import me.alex.coinsapi.implementation.CoinsAPI;
import me.alex.coinsapi.implementation.data.Messages;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

//@Permission("coinsapi.command.coins")
@CommandHandler("coins|coin")
public class CoinsCommand extends Command {

    private final CoinUserDAO dao;

    public CoinsCommand(CoinsAPI plugin) {
        this.dao = plugin.getDatabase();
    }

    @Override
    public void execute(@NotNull Context context) {
        if (context.getArgs().length == 0) {
            if(context.getSender() instanceof User user) {
                getCommand(context, user.getName());
            } else {
                context.getSender().sendMessage(getErrorMessage());
            }
            return;
        }
        switch (context.getArgs()[0]) {
            case "help", "hilfe" -> helpCommand(context);
            case "add" -> addCommand(context);
            case "delete" -> deleteCommand(context);
            case "remove" -> removeCommand(context);
            case "set" -> setCommand(context);
            case "get", "coins" -> {
                String name = context.getArgs().length == 1 ? context.getSender().getName() : context.getArgs()[1];
                getCommand(context, name);
            }
            default -> helpCommand(context);
        }
    }

    private void removeCommand(Context context) {
        String[] args = context.getArgs();
        //the command is /coins remove <player> [coins] and use early return
        if (args.length < 2) {
            context.getSender().sendMessage(getErrorMessage());
            return;
        }
        //Number
        long value;
        try {
            value = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            context.getSender().sendMessage(Messages.PREFIX
                    .append(Component.translatable("coinsapi.error.notNumber")
                            .args(Component.text(args[2]))));
            return;
        }

        //User
        CompletableFuture<Optional<CoinUser>> user = dao.getUserAsync(args[1]);
        user.whenComplete((coinUser, throwable) -> {
            if (sendErrorMessage(context, throwable)) return;

            if (coinUser.isEmpty()) {
                context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.error.userNotFound")));
                return;
            }

            CoinUser user1 = coinUser.get();
            long oldCoins = user1.getCoins();
            user1.setCoins(oldCoins - value);
            boolean success = dao.saveUser(user1);
            if (!success) {
                context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.error.save")));
                return;
            }
            //Message: The user {user} had {oldCoins} and now has {newCoins}
            context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.addCoins")
                    .args(
                            Component.text(user1.getLastKnownName()),
                            Component.text(oldCoins),
                            Component.text(user1.getCoins())))
            );
        });
    }

    private void getCommand(Context context, String player) {
        //the command is /coins get <player> and use early return
        CompletableFuture<Optional<CoinUser>> user = dao.getUserAsync(player);
        user.whenComplete((coinUser, throwable) -> {
            if (sendErrorMessage(context, throwable)) return;

            if (coinUser.isEmpty()) {
                context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.error.userNotFound")));
                return;
            }
            //Message:
            CoinUser user1 = coinUser.get();
            context.getSender().sendMessage(Messages.PREFIX
                    .append(Component.translatable("coinsapi.getCoins")
                            .args(Component.text(user1.getLastKnownName()), Component.text(user1.getCoins()))));
        });
    }

    private void setCommand(Context context) {
        String[] args = context.getArgs();
        //the command is /coins set <player> <coins> and use early return
        if (args.length < 2) {
            context.getSender().sendMessage(getErrorMessage());
            return;
        }
        //Check if it is a number
        long value;
        try {
            value = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            context.getSender().sendMessage(Messages.PREFIX
                    .append(Component.translatable("coinsapi.error.notNumber")
                            .args(Component.text(args[2]))));
            return;
        }
        //Set the coins
        CompletableFuture<Optional<CoinUser>> user = dao.getUserAsync(args[1]);
        user.whenComplete((coinUser, throwable) -> {
            if (sendErrorMessage(context, throwable)) return;

            if (coinUser.isEmpty()) {
                context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.error.userNotFound")));
                return;
            }
            CoinUser user1 = coinUser.get();
            long oldCoins = user1.getCoins();
            user1.setCoins(value);

            boolean success = dao.saveUser(user1);
            if (!success) {
                context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.error.save")));
                return;
            }

            context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.setCoins")
                    .args(
                            Component.text(user1.getLastKnownName()),
                            Component.text(user1.getCoins()),
                            Component.text(oldCoins)))
            );
        });
    }

    private void deleteCommand(Context context) {
        String[] args = context.getArgs();
        //the command is /coins delete <player> and use early return
        if (args.length < 2) {
            context.getSender().sendMessage(getErrorMessage());
            return;
        }
        CompletableFuture<Optional<CoinUser>> user = dao.getUserAsync(args[1]);
        user.whenComplete((coinUser, throwable) -> {
            if (sendErrorMessage(context, throwable)) return;

            if (coinUser.isEmpty()) {
                context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.error.userNotFound")));
                return;
            }
            boolean isSuccess = dao.deleteUser(coinUser.get());
            if (!isSuccess) {
                context.getSender().sendMessage(getErrorMessage());
                return;
            }
            context.getSender().sendMessage(Messages.PREFIX
                    .append(Component.translatable("coinsapi.removeUser")
                            .args(Component.text(coinUser.get().getLastKnownName()))));
        });
    }

    private void addCommand(Context context) {
        String[] args = context.getArgs();
        //the command is /coins delete <player> and use early return
        if (args.length < 2) {
            context.getSender().sendMessage(getErrorMessage());
            return;
        }
        //Check if it is a number
        long value;
        try {
            value = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            context.getSender().sendMessage(Messages.PREFIX
                    .append(Component.translatable("coinsapi.error.notNumber")
                            .args(Component.text(args[2]))));
            return;
        }
        CompletableFuture<Optional<CoinUser>> user = dao.getUserAsync(args[1]);
        user.whenComplete((coinUser, throwable) -> {
            if (sendErrorMessage(context, throwable)) return;

            if (coinUser.isEmpty()) {
                context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.error.userNotFound")));
                return;
            }
            CoinUser user1 = coinUser.get();
            long oldCoins = user1.getCoins();
            user1.setCoins(oldCoins + value);
            boolean success = dao.saveUser(user1);
            if (!success) {
                context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.error.save")));
                return;
            }
            //Message: The user {user} had {oldCoins} and now has {newCoins}
            context.getSender().sendMessage(Messages.PREFIX.append(Component.translatable("coinsapi.addCoins")
                    .args(
                            Component.text(user1.getLastKnownName()),
                            Component.text(oldCoins),
                            Component.text(user1.getCoins())))
            );
        });
    }

    private void helpCommand(Context context) {
        context.getSender().sendMessage(getHelpMessage());
    }

    private Component getHelpMessage() {
        return Messages.PREFIX.append(Component.translatable("coinsapi.help"));
    }

    private Component getErrorMessage() {
        return Messages.PREFIX.append(Component.translatable("coinsapi.error.moreArgs"));
    }

    private boolean sendErrorMessage(Context context, @Nullable Throwable e) {
        boolean is = e != null;
        if (is) {
            String shortMessage = e.getStackTrace()[0].toString();
            context.getSender().sendMessage(Messages.PREFIX.append(Component
                    .translatable("coinsapi.error")
                    .args(Component.text(shortMessage))));
        }
        return is;
    }
}
