package com.github.mike.commands;


import com.github.mike.database.CacheLayer;
import org.javacord.api.event.message.MessageCreateEvent;

public class PingCommand implements CommandExecutor.Command {


    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        event.getMessage().getChannel().sendMessage("Pong!");
    }
}