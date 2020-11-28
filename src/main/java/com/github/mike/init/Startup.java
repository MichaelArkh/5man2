package com.github.mike.init;

import com.github.mike.commands.CommandExecutor;
import com.github.mike.commands.MessageSender;
import com.github.mike.database.CacheLayer;
import com.github.mike.database.DBModify;
import org.javacord.api.DiscordApi;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Startup {
    private DiscordApi api;
    private DBModify mod;

    public Startup(DiscordApi api, DBModify mod) {
        this.api = api;
        this.mod = mod;
        registerListeners();
    }

    public void registerListeners() {
        CacheLayer cacheLayer = new CacheLayer(mod);
        CommandExecutor executor = new CommandExecutor(mod, cacheLayer);
        api.addServerJoinListener(e -> {
            e.getApi().getThreadPool().getExecutorService().submit(() -> {
                mod.checkIfThere(e.getServer().getIdAsString(), Long.toString(e.getServer().getOwnerId()));
            });
        });
        api.addServerLeaveListener(e -> {
            e.getApi().getThreadPool().getExecutorService().submit(() -> {
                mod.leftServer(e.getServer().getIdAsString());
            });
        });
        api.addMessageCreateListener(e -> {
            e.getApi().getThreadPool().getExecutorService().submit(() -> {
                executor.receiveMessage(e);
            });
        });

        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 2);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        if (date.getTime().before(new Date())) {
            date.add(Calendar.DATE, 1);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                api.getServers().forEach(e -> {
                    cacheLayer.clearReserves(e.getIdAsString());
                    new MessageSender().execute(api.getTextChannelById(cacheLayer.getChannel(e.getIdAsString())).get(), e, cacheLayer);
                });
            }
        }, date.getTime(), 1000 * 60 * 60 * 24);
        api.getServers().forEach(e -> mod.checkIfThere(e.getIdAsString(), Long.toString(e.getOwnerId())));
        api.getServers().forEach(e -> e.getTextChannelById(cacheLayer.getChannel(e.getIdAsString()))
                .ifPresent(f -> cacheLayer.putMessage(e.getIdAsString(), f.getPins())));
    }
}
