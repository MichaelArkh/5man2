package com.github.mike.commands.reserve_sub;


import com.github.mike.commands.CommandExecutor;
import com.github.mike.database.CacheLayer;
import org.javacord.api.event.message.MessageCreateEvent;

public class Clear implements CommandExecutor.Command {

    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        cacheLayer.clearReserves(event.getServer().get().getIdAsString());
        event.getChannel().sendMessage(":exclamation:Cleared the list");
    }
}
