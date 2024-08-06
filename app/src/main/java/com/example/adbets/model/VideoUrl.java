package com.example.adbets.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "VideoUrl")
public class VideoUrl {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
