package com.github.mike.commands;

import com.github.mike.database.CacheLayer;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageSender implements CommandExecutor.Command {

    @Override
    public void execute(String[] args, MessageCreateEvent event, CacheLayer cacheLayer) {
        execute(event.getChannel(), event.getServer().get(), cacheLayer);
    }

    public void execute(TextChannel chan, Server serv, CacheLayer cacheLayer) {
        if(cacheLayer.getMessage(serv.getIdAsString()) != null){
            Message.edit(serv.getApi(), chan.getIdAsString(), cacheLayer.getMessage(serv.getIdAsString()), buildEmbed(serv, cacheLayer));
        } else {
            chan.sendMessage(buildEmbed(serv, cacheLayer)).whenComplete((message, throwable) -> {
                message.pin();
                cacheLayer.putMessage(serv.getIdAsString(), message);
            });

        }
    }

    public EmbedBuilder buildEmbed(Server serv, CacheLayer layer) {
        EmbedBuilder a = new EmbedBuilder();
        a.setColor(new Color(126, 163, 222));
        a.setTitle("RESERVES");

        AtomicInteger index = new AtomicInteger(1);
        layer.getReserves(serv.getIdAsString()).forEach(e -> {
            Timestamp b = e.getDate();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss a");
            String nick = serv.getMemberById(e.getId()).map(f -> f.getNickname(serv).orElse(serv.getMemberById(e.getId()).map(Nameable::getName).get())).get();
            a.addField(String.valueOf(index.getAndIncrement()) + " " + nick, "` " + dtf.format(b.toLocalDateTime()) + "`");
            //a.addInlineField(String.valueOf(index.getAndIncrement()), "\u200B");
            //a.addInlineField(nick, dtf.format(b.toLocalDateTime()) + "");
            //a.addInlineField("\u200B", "\u200B");
        });
        a.setTimestampToNow();
        //a.setAuthor("5man");
        return a;
    }
}
