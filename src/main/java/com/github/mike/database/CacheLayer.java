package com.github.mike.database;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CacheLayer {
    private DBModify db;
    private Map<String, String> prefixCache;
    private Map<String, List<String>> adminCache;
    private Map<String, String> channelCache;
    private Map<String, List<ReservePlayer>> reserveCache;
    private Map<String, String> messageCache;

    public CacheLayer(DBModify db) {
        this.db = db;
        prefixCache = db.getPrefixes();
        adminCache = db.getAllPerms();
        channelCache = db.getAllChannels();
        reserveCache = new HashMap<String, List<ReservePlayer>>();
        List<String> a = db.getAllGuilds();
        a.forEach(e -> reserveCache.put(e, db.getReserves(e)));
        messageCache = new HashMap<String, String>();
    }

    public String getPrefix(String guildID) {
        return prefixCache.get(guildID);
    }

    public List<String> getPerms(String guildID) {
        return adminCache.get(guildID);
    }

    public boolean changePrefix(String guildID, String prefix) {
        db.changePrefix(guildID, prefix);
        prefixCache.put(guildID, prefix);
        return true;
    }

    public boolean reserve(String guildID, String playerId) throws SQLException {
        List<ReservePlayer> a = reserveCache.get(guildID);
        List<ReservePlayer> counted = a.stream().filter(e -> !e.getId().equals(playerId)).collect(Collectors.toList());
        if (a.size() - counted.size() > 0) {
            db.removePlayer(guildID, playerId);
            reserveCache.put(guildID, counted);
            return false;
        } else {
            ReservePlayer player = db.addPlayer(guildID, playerId);
            a.add(player);
            return true;
        }
    }

    public void clearReserves(String guildID) {
        reserveCache.put(guildID, new ArrayList<ReservePlayer>());
        db.clearReserves(guildID);
    }

    public List<ReservePlayer> getReserves(String guildID) {
        return reserveCache.get(guildID);
    }

    public void putMessage(String guildId, CompletableFuture<MessageSet> m){
        m.whenComplete((e, g) -> {
            if(e.getOldestMessage().isPresent()){
                messageCache.put(guildId, e.getOldestMessage().get().getIdAsString());
                e.getOldestMessage().get().addMessageDeleteListener(f -> messageCache.remove(guildId));
            }
        });
    }
    public void putMessage(String guildId, Message m){
        messageCache.put(guildId, m.getIdAsString());
        m.addMessageDeleteListener(e -> messageCache.remove(guildId));
    }

    public String getMessage(String guildId){
        return messageCache.get(guildId);
    }

    public boolean changePerms(String guildID, String playerId) {
        boolean ret = db.perms(guildID, playerId);
        if (ret) {
            List<String> ok = adminCache.get(guildID);
            ok.add(playerId);
            adminCache.put(guildID, ok);
        } else {
            List<String> ok = adminCache.get(guildID);
            ok.remove(playerId);
            adminCache.put(guildID, ok);
        }
        return ret;
    }

    public String getChannel(String guildID) {
        return channelCache.get(guildID);
    }

    public boolean hasPerms(String guildID, String playerID) {
        List<String> a = adminCache.get(guildID);
        return a.contains(playerID);
    }

    public void changeChannel(String guildID, String newChannel, DiscordApi api) {
        db.changeChannel(guildID, newChannel);
        Message.delete(api, channelCache.get(guildID), messageCache.get(guildID));
        channelCache.put(guildID, newChannel);
        messageCache.remove(guildID);
    }

    public Map<String, String> getPrefixCache() {
        return prefixCache;
    }

    public void setPrefixCache(Map<String, String> prefixCache) {
        this.prefixCache = prefixCache;
    }

    public Map<String, List<String>> getAdminCache() {
        return adminCache;
    }

    public void setAdminCache(Map<String, List<String>> adminCache) {
        this.adminCache = adminCache;
    }
}
