package com.github.mike.database;

import java.sql.Timestamp;

public class ReservePlayer {
    private String id;
    private Timestamp date;

    public ReservePlayer(String id, Timestamp date) {
        this.id = id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
