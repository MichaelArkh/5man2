package com.github.mike.commands;

import com.github.mike.database.CacheLayer;
import com.github.mike.database.DBModify;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.HashMap;

public class CommandExecutor {
    private HashMap<String, Command> commands = new HashMap<String, Command>();

    private DBModify db;
    private CacheLayer cacheLayer;

    public interface Command {
        void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer);
    }

    public CommandExecutor(DBModify db, CacheLayer layer) {
        this.db = db;
        commands.put("clear", new Clear());
        commands.put("ping", new PingCommand());
        commands.put("r", new Reserve(db));
        this.cacheLayer = layer;
    }

    public void receiveMessage(MessageCreateEvent event) {

        if (event.getServer().isPresent()) {

            String prefix = cacheLayer.getPrefix(event.getServer().get().getIdAsString());
            if (!event.getMessage().getContent().startsWith(prefix)) {
                return;
            }
            String[] args = event.getMessage().getContent().split(" ");
            if (commands.containsKey(args[0].substring(1))) {
                commands.get(args[0].substring(1)).execute(args, event, cacheLayer);
            } else {
                //Unknown command
            }
        } else {
            //Not in a server
        }
    }
}
