package com.nhom13.learningenglishapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.nhom13.learningenglishapp.database.DatabaseHelper;
import com.nhom13.learningenglishapp.database.models.Chapter;
import com.nhom13.learningenglishapp.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class ChapterDao {
    private DatabaseHelper dbHelper;
    private Context context;

    public ChapterDao(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean insertChapter(Chapter chapter, Uri imageUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Save image to local storage and get the path
            String imagePath = "";
            if (imageUri != null) {
                imagePath = FileUtils.saveImageToInternalStorage(context, imageUri, "chapter_" + chapter.getId());
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_CHAPTER_ID, chapter.getId());
            values.put(DatabaseHelper.KEY_CHAPTER_NAME, chapter.getName());
            values.put(DatabaseHelper.KEY_CHAPTER_IMAGE_PATH, imagePath);

            // Insert the new row
            long id = db.insert(DatabaseHelper.TABLE_CHAPTERS, null, values);
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

    public List<Chapter> getAllChapters() {
        List<Chapter> chapters = new ArrayList<>();

        String CHAPTERS_SELECT_QUERY = "SELECT * FROM " + DatabaseHelper.TABLE_CHAPTERS;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(CHAPTERS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Chapter chapter = new Chapter();
                    chapter.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_CHAPTER_ID)));
                    chapter.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_CHAPTER_NAME)));
                    chapter.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_CHAPTER_IMAGE_PATH)));

                    chapters.add(chapter);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return chapters;
    }

    public Chapter getChapterById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_CHAPTERS +
                " WHERE " + DatabaseHelper.KEY_CHAPTER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Chapter chapter = null;
        try {
            if (cursor.moveToFirst()) {
                chapter = new Chapter();
                chapter.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_CHAPTER_ID)));
                chapter.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_CHAPTER_NAME)));
                chapter.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_CHAPTER_IMAGE_PATH)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return chapter;
    }

    public boolean updateChapter(Chapter chapter, Uri newImageUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_CHAPTER_NAME, chapter.getName());

            // Update image if a new one is provided
            if (newImageUri != null) {
                String imagePath = FileUtils.saveImageToInternalStorage(context, newImageUri, "chapter_" + chapter.getId());
                values.put(DatabaseHelper.KEY_CHAPTER_IMAGE_PATH, imagePath);
            }

            // Updating row
            int result = db.update(DatabaseHelper.TABLE_CHAPTERS, values,
                    DatabaseHelper.KEY_CHAPTER_ID + " = ?",
                    new String[]{String.valueOf(chapter.getId())});

            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteChapter(int chapterId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // First get the chapter to retrieve the image path
        Chapter chapter = getChapterById(chapterId);

        // Delete the chapter from database
        boolean success = db.delete(DatabaseHelper.TABLE_CHAPTERS,
                DatabaseHelper.KEY_CHAPTER_ID + " = ?",
                new String[]{String.valueOf(chapterId)}) > 0;

        // If successful and there's an image, delete it from storage
        if (success && chapter != null && chapter.getImagePath() != null && !chapter.getImagePath().isEmpty()) {
            FileUtils.deleteImageFromInternalStorage(context, chapter.getImagePath());
        }

        return success;
    }

    // Trong class ChapterDao

    // Lấy tổng số chương
    public int getTotalChapterCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_CHAPTERS;
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

}