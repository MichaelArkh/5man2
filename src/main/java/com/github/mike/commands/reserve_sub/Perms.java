package com.github.mike.commands.reserve_sub;

import com.github.mike.commands.CommandExecutor;
import com.github.mike.database.CacheLayer;
import org.javacord.api.event.message.MessageCreateEvent;

public class Perms implements CommandExecutor.Command {

    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        if (args.length > 2) {

            boolean ret = cacheLayer.changePerms(event.getServer().get().getIdAsString(), event.getMessage().getMentionedUsers().get(0).getIdAsString());
            event.getChannel().sendMessage(ret ? ":exclamation:Added " + event.getMessage().getMentionedUsers().get(0).getDisplayName(event.getServer().get()) + " to perms" : ":exclamation:Removed " + event.getMessage().getMentionedUsers().get(0).getDisplayName(event.getServer().get()) + " from perms");
        }
    }
}
