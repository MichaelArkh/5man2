package com.github.mike.database;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.*;
import java.util.*;

public class DBModify {
    private Connect connect;

    public DBModify(Connect conn) {
        this.connect = conn;
    }


    public boolean reserve(String guildId, String playerId) throws SQLException {
            if (isPlayerReserved(guildId, playerId)) {
                removePlayer(guildId, playerId);
                return false;
            } else {
                addPlayer(guildId, playerId);
                return true;
            }
    }

    public Map<String, String> getPrefixes() {
        HashMap<String, String> ret = new HashMap<String, String>();
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select * from settings");
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                ret.put(res.getString("guildID"), res.getString("prefix"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public List<String> getAllGuilds() {
        List<String> ret = new ArrayList<String>();
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select guildId from settings");
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                String guild = res.getString("guildId");
                ret.add(guild);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void checkIfThere(String guildID, String ownerID) {
        if (!readSettings(guildID)) {
            putDefaults(guildID, ownerID);
        }
        //Check if there is a reservestable
        if (!checkTable(guildID)) {
            createTableWithDefaults(guildID);
        }
    }

    public void leftServer(String guildID) {
        clearSettings(guildID);
        deleteTable(guildID);
    }

    private void clearSettings(String guildID) {
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Delete FROM settings where guildID = ?");
            stmt.setString(1, guildID);
            int row = stmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTable(String guildID) {
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Drop table `" + guildID + "`");
            boolean row = stmt.execute();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void removePlayer(String guildId, String playerId) throws SQLException {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Delete FROM `" + guildId + "` where userid = ?");
            stmt.setString(1, playerId);
            int row = stmt.executeUpdate();
            conn.close();
    }

    ReservePlayer addPlayer(String guildId, String playerId) throws SQLException {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("INSERT into `" + guildId + "` (userid, date) values (?, ?)");
            ReservePlayer a = new ReservePlayer(playerId, new Timestamp(System.currentTimeMillis()));
            stmt.setString(1, playerId);
            stmt.setTimestamp(2, a.getDate());
            int row = stmt.executeUpdate();
            conn.close();
            return a;
    }

    public void clearReserves(String guildId) {
        String sql = "Delete from `" + guildId + "`";
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement(sql);
            int res = stmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean perms(String guildId, String playerId) {
        if (hasPerms(guildId, playerId)) {
            removePerms(guildId, playerId);
            return false;
        } else {
            addPerms(guildId, playerId);
            return true;
        }
    }

    public void addPerms(String guildId, String playerId) {
        String perms = getPerms(guildId);
        Gson gson = new Gson();
        ArrayList<String> ok = gson.fromJson(perms, new TypeToken<List<String>>() {
        }.getType());
        ok.add(playerId);
        writePerms(guildId, ok);
    }

    public Map<String, String> getAllChannels() {
        HashMap<String, String> ret = new HashMap<String, String>();
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select * from settings");
            ResultSet res = stmt.executeQuery();
            Gson gson = new Gson();
            while (res.next()) {
                String perms = res.getString("reserveId");
                ret.put(res.getString("guildId"), perms);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void removePerms(String guildId, String playerId) {
        String perms = getPerms(guildId);
        Gson gson = new Gson();
        ArrayList<String> ok = gson.fromJson(perms, new TypeToken<List<String>>() {
        }.getType());
        ok.remove(playerId);
        writePerms(guildId, ok);
    }

    public boolean hasPerms(String guildId, String playerId) {
        String perms = getPerms(guildId);
        Gson gson = new Gson();
        ArrayList<String> ok = gson.fromJson(perms, new TypeToken<List<String>>() {
        }.getType());
        return ok.contains(playerId);
    }

    public void writePerms(String guildId, List<String> perms) {
        boolean ret = true;
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Update settings set perms = ? where guildId = ? Limit 1");
            Gson gson = new Gson();
            stmt.setString(1, gson.toJson(perms));
            stmt.setString(2, guildId);
            int res = stmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPerms(String guildId) {
        String ret = "";
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select perms from settings where guildId = ? Limit 1");
            stmt.setString(1, guildId);
            ResultSet res = stmt.executeQuery();

            if (!res.next()) {

            } else {
                ret =  res.getString("perms");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Map<String, List<String>> getAllPerms() {
        HashMap<String, List<String>> ret = new HashMap<String, List<String>>();
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select * from settings");
            ResultSet res = stmt.executeQuery();
            Gson gson = new Gson();
            while (res.next()) {
                String perms = res.getString("perms");
                ArrayList<String> ok = gson.fromJson(perms, new TypeToken<List<String>>() {
                }.getType());
                ret.put(res.getString("guildId"), ok);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean isPlayerReserved(String guildId, String playerId) {
        String sql = "Select * from `" + guildId + "` where userid = ? Limit 1";
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, playerId);
            ResultSet res = stmt.executeQuery();
            if (!res.next()) {
                conn.close();
                return false;
            }
            conn.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ReservePlayer> getReserves(String guildId) {
        List<ReservePlayer> players = new ArrayList<ReservePlayer>();
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select * from `" + guildId + "`");
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                ReservePlayer a = new ReservePlayer(res.getString("userid"), res.getTimestamp("date"));
                players.add(a);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return players;
    }

    public void createTableWithDefaults(String guildId) {
        String query = "CREATE TABLE `" + guildId + "` (" +
                " userid varchar(18) PRIMARY KEY NOT NULL," +
                " date datetime NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.execute();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkTable(String guildID) {
        boolean ret = true;
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Show tables like ?");
            stmt.setString(1, guildID);
            ResultSet res = stmt.executeQuery();
            if (!res.next()) {
                conn.close();
                return false;
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    public void changeChannel(String guildId, String channelId) {
        boolean ret = true;
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Update settings set reserveId = ?, lastUpdate = ? where guildId = ? Limit 1");
            stmt.setString(1, channelId);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, guildId);
            int res = stmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getChannel(String guildId) {
        String ret = ".";
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select reserveId from settings where guildId = ? Limit 1");
            stmt.setString(1, guildId);
            ResultSet res = stmt.executeQuery();
            if (!res.next()) {

            } else {
                ret = res.getString("reserveId");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String getPrefix(String guildId) {
        String ret = ".";
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select prefix from settings where guildId = ? Limit 1");
            stmt.setString(1, guildId);
            ResultSet res = stmt.executeQuery();
            if (!res.next()) {

            } else {
                ret = res.getString("prefix");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void changePrefix(String guildId, String newPrefix) {
        boolean ret = true;
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Update settings set prefix = ?, lastUpdate = ? where guildId = ? Limit 1");
            stmt.setString(1, newPrefix);
            stmt.setString(3, guildId);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            int res = stmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void putDefaults(String guildId, String owner) {
        boolean ret = true;
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("INSERT into settings (guildId, reserveId, perms, lastUpdate) values (?, ?, ?, ?)");
            stmt.setString(1, guildId);
            stmt.setString(2, "");
            Gson gson = new Gson();
            stmt.setString(3, gson.toJson(Collections.singletonList(owner)));
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            int row = stmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean readSettings(String guildID) {
        boolean ret = true;
        try {
            Connection conn = connect.getConn();
            PreparedStatement stmt = conn.prepareStatement("Select * from settings where guildId = ? Limit 1");
            stmt.setString(1, guildID);
            ResultSet res = stmt.executeQuery();
            if (!res.next()) {
                ret = false;
            } else {

            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
