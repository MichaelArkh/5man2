package com.github.mike.commands;

import com.github.mike.database.CacheLayer;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.stream.Collectors;

public class Clear implements CommandExecutor.Command {

    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        if (args.length > 1 && cacheLayer.hasPerms(event.getServer().get().getIdAsString(), event.getMessage().getAuthor().getIdAsString())) {
            event.getChannel().deleteMessages(event.getChannel().getMessagesAsStream().limit(Integer.parseInt(args[1])).filter(e -> !e.isPinned()).collect(Collectors.toList()));
            //event.getChannel().getMessagesAsStream().limit(Integer.parseInt(args[1])).forEach(Message::delete);
        } else {
            event.getChannel().sendMessage(":grey_exclamation:Invalid Args!");
        }
    }

    public void execute(String[] args, MessageCreateEvent event, boolean f) {
        if (args.length > 1) {
            event.getChannel().deleteMessages(event.getChannel().getMessagesAsStream().limit(Integer.parseInt(args[1])).filter(e -> !e.isPinned()).collect(Collectors.toList()));
            //event.getChannel().getMessagesAsStream().limit(Integer.parseInt(args[1])).forEach(Message::delete);
        } else {
            event.getChannel().sendMessage(":grey_exclamation:Invalid Args!");
        }
    }
}
