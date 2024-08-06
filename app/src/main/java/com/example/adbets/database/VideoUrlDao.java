package com.example.adbets.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.adbets.model.VideoUrl;
import java.util.List;

@Dao
public interface VideoUrlDao {
    @Insert
    void insert(VideoUrl videoUrl);

    @Update
    void update(VideoUrl videoUrl);

    @Insert
    void insertVideoUrl(List<VideoUrl> videoUrlList);

    @Delete
    void delete(VideoUrl videoUrl);

    @Query("SELECT * FROM VideoUrl")
    List<VideoUrl> getAllVideoUrl();

    @Query("DELETE FROM VideoUrl")
    void deleteAll();

}
