package com.github.mike.commands;

import com.github.mike.database.CacheLayer;
import com.github.mike.database.DBModify;
import org.javacord.api.event.message.MessageCreateEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Reserve implements CommandExecutor.Command {
    private HashMap<String, CommandExecutor.Command> reserveSubCommands = new HashMap<String, CommandExecutor.Command>();
    private HashMap<String, Boolean> sendMess = new HashMap<String, Boolean>();
    DBModify db;


    public Reserve(DBModify db) {
        this.db = db;
        reserveSubCommands.put("clear", new com.github.mike.commands.reserve_sub.Clear());
        reserveSubCommands.put("force", new com.github.mike.commands.reserve_sub.Add());
        reserveSubCommands.put("f", new com.github.mike.commands.reserve_sub.Add());
        reserveSubCommands.put("c", new com.github.mike.commands.reserve_sub.Clear());
        reserveSubCommands.put("chan", new com.github.mike.commands.reserve_sub.Chan());
        reserveSubCommands.put("prefix", new com.github.mike.commands.reserve_sub.Prefix());
        reserveSubCommands.put("perms", new com.github.mike.commands.reserve_sub.Perms());
        sendMess.put("clear", true);
        sendMess.put("force", true);
        sendMess.put("f", true);
        sendMess.put("c", true);

    }

    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        TimerTask task = new TimerTask() {
            public void run() {
                new Clear().execute(new String[]{" ", "10"}, event, false);
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 5000L;


        if (!event.getServer().isPresent() || (!event.getChannel().getIdAsString().equals(cacheLayer.getChannel(event.getServer().get().getIdAsString())) && args.length < 2)) {
            event.getChannel().sendMessage(":grey_exclamation:You must do this in the right channel!");
            return;
        }
        if (args.length < 2) {
            try {
                boolean ret = cacheLayer.reserve(event.getServer().get().getIdAsString(), event.getMessageAuthor().getIdAsString());
                event.getChannel().sendMessage(ret ? ":exclamation:Added " + event.getMessageAuthor().getDisplayName() + " to reserves" : ":exclamation:Removed " + event.getMessageAuthor().getDisplayName() + " from reserves");
            } catch (SQLException e) {
                e.printStackTrace();
                event.getChannel().sendMessage(":exclamation:Try again!");
            }
            timer.schedule(task, delay);
            new MessageSender().execute(args, event, cacheLayer);
        } else {
            if (reserveSubCommands.containsKey(args[1]) && cacheLayer.hasPerms(event.getServer().get().getIdAsString(), event.getMessage().getAuthor().getIdAsString())) {
                reserveSubCommands.get(args[1]).execute(args, event, cacheLayer);
                timer.schedule(task, delay);
                if (sendMess.containsKey(args[1]))
                    new MessageSender().execute(args, event, cacheLayer);
            } else {
                //Unknown command
            }
        }

    }
}
