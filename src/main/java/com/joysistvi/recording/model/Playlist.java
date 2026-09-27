package com.joysistvi.recording.model;

import java.time.LocalDateTime;

public class Playlist {
    private int id;
    private String name;
    private LocalDateTime dateCreated;
    private int userId;

    public Playlist(int id, LocalDateTime dateCreated, int userId) {
        this(id, null, dateCreated, userId);
    }

    public Playlist(String name, int userId) {
        this.name = name;
        this.dateCreated = LocalDateTime.now();
        this.userId = userId;
    }

    public Playlist(int id, String name, LocalDateTime dateCreated, int userId) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
