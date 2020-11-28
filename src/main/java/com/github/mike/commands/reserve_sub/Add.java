package com.github.mike.commands.reserve_sub;

import com.github.mike.commands.CommandExecutor;
import com.github.mike.database.CacheLayer;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.sql.SQLException;
import java.util.List;

public class Add implements CommandExecutor.Command {

    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        if (args.length < 4) {
            List<User> mentioned = event.getMessage().getMentionedUsers();
            try {
                boolean ret = cacheLayer.reserve(event.getServer().get().getIdAsString(), mentioned.get(0).getIdAsString());
                event.getChannel().sendMessage(ret ? ":exclamation:Added " + mentioned.get(0).getNickname(event.getServer().get()).orElse(mentioned.get(0).getDisplayName(event.getServer().get())) + " to reserves" : ":exclamation:Removed " + mentioned.get(0).getNickname(event.getServer().get()).orElse(mentioned.get(0).getDisplayName(event.getServer().get())) + " from reserves");
            } catch (SQLException e){
                event.getChannel().sendMessage(":exclamation:Try again!");
            }
        } else {
            event.getChannel().sendMessage(":grey_exclamation:Add who?");
        }
    }
}
