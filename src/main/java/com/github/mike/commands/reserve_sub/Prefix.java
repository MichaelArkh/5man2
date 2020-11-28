package com.github.mike.commands.reserve_sub;

import com.github.mike.commands.CommandExecutor;
import com.github.mike.database.CacheLayer;
import org.javacord.api.event.message.MessageCreateEvent;

public class Prefix implements CommandExecutor.Command {

    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        if (args.length > 2) {
            String prefix = args[2];
            cacheLayer.changePrefix(event.getServer().get().getIdAsString(), prefix);
            event.getChannel().sendMessage(":exclamation:Changed prefix");

        } else {

        }
    }
}
