package com.nhom13.learningenglishapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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


            long id = db.insert(DatabaseHelper.TABLE_VIDEOS, null, values);
            return id != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {

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
                    video.setViewCount(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_VIEW_COUNT))); // <<<<<<<<<< LẤY VIEW COUNT
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
                video.setViewCount(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_VIEW_COUNT))); // <<<<<<<<<< LẤY VIEW COUNT
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

    public int getTotalVideoCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_VIDEOS;
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }


    public boolean incrementViewCount(int videoId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String updateQuery = "UPDATE " + DatabaseHelper.TABLE_VIDEOS +
                    " SET " + DatabaseHelper.KEY_VIDEO_VIEW_COUNT + " = " + DatabaseHelper.KEY_VIDEO_VIEW_COUNT + " + 1" +
                    " WHERE " + DatabaseHelper.KEY_VIDEO_ID + " = " + videoId;
            db.execSQL(updateQuery);
            Log.d("VideoDao", "Incremented view count for video ID: " + videoId);
            return true;
        } catch (Exception e) {
            Log.e("VideoDao", "Error incrementing view count for video ID: " + videoId, e);
            return false;
        }

    }

    public List<Video> getTopViewedVideos(int limit) {
        List<Video> videoList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_VIDEOS +
                " ORDER BY " + DatabaseHelper.KEY_VIDEO_VIEW_COUNT + " DESC, " + DatabaseHelper.KEY_VIDEO_TITLE + " ASC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Video video = new Video();
                    video.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_ID)));
                    video.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_TITLE)));
                    video.setThumbnailUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_THUMBNAIL)));
                    video.setVideoUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_URL)));
                    video.setViewCount(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VIDEO_VIEW_COUNT)));
                    videoList.add(video);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("VideoDao", "Error getting top viewed videos", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return videoList;
    }

}