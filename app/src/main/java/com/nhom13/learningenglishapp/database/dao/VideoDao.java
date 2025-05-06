package com.nhom13.learningenglishapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nhom13.learningenglishapp.database.DatabaseHelper;
import com.nhom13.learningenglishapp.database.models.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoDao {
    private DatabaseHelper dbHelper;
    private Context context;

    public VideoDao(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean insertVideo(Video video) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_VIDEO_TITLE, video.getTitle());
            values.put(DatabaseHelper.KEY_VIDEO_THUMBNAIL, video.getThumbnailUrl());
            values.put(DatabaseHelper.KEY_VIDEO_URL, video.getVideoUrl());

            // Insert the new row
            long id = db.insert(DatabaseHelper.TABLE_VIDEOS, null, values);
            return id != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<Video> getAllVideos() {
        List<Video> videos = new ArrayList<>();

        String VIDEOS_SELECT_QUERY = "SELECT * FROM " + DatabaseHelper.TABLE_VIDEOS;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(VIDEOS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Video video = new Video();
                    video.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_ID)));
                    video.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_TITLE)));
                    video.setThumbnailUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_THUMBNAIL)));
                    video.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_URL)));

                    videos.add(video);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return videos;
    }

    public Video getVideoById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_VIDEOS +
                " WHERE " + DatabaseHelper.KEY_VIDEO_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Video video = null;
        try {
            if (cursor.moveToFirst()) {
                video = new Video();
                video.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_ID)));
                video.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_TITLE)));
                video.setThumbnailUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_THUMBNAIL)));
                video.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_URL)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return video;
    }

    public boolean updateVideo(Video video) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_VIDEO_TITLE, video.getTitle());
            values.put(DatabaseHelper.KEY_VIDEO_THUMBNAIL, video.getThumbnailUrl());
            values.put(DatabaseHelper.KEY_VIDEO_URL, video.getVideoUrl());

            // Updating row
            int result = db.update(DatabaseHelper.TABLE_VIDEOS, values,
                    DatabaseHelper.KEY_VIDEO_ID + " = ?",
                    new String[]{String.valueOf(video.getId())});

            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVideo(int videoId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete the video from database
        boolean success = db.delete(DatabaseHelper.TABLE_VIDEOS,
                DatabaseHelper.KEY_VIDEO_ID + " = ?",
                new String[]{String.valueOf(videoId)}) > 0;

        return success;
    }
}