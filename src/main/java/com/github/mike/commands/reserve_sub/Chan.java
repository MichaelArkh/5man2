package com.github.mike.commands.reserve_sub;

import com.github.mike.commands.CommandExecutor;
import com.github.mike.commands.MessageSender;
import com.github.mike.database.CacheLayer;
import org.javacord.api.event.message.MessageCreateEvent;

public class Chan implements CommandExecutor.Command {

    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        if (args.length > 2) {
            String chan_id = args[2];
            if(event.getApi().getTextChannelById(chan_id).isPresent()) {
                cacheLayer.changeChannel(event.getServer().get().getIdAsString(), chan_id, event.getApi());
                event.getChannel().sendMessage(":exclamation:Changed channel");
                new MessageSender().execute(event.getApi().getTextChannelById(chan_id).get(), event.getServer().get(), cacheLayer);
            } else {
                event.getChannel().sendMessage(":grey_exclamation:Channel id not valid!");
            }
        } else {

        }
    }
}
