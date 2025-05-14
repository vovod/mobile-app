package com.nhom13.learningenglishapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.nhom13.learningenglishapp.database.DatabaseHelper;
import com.nhom13.learningenglishapp.database.models.Chapter;
import com.nhom13.learningenglishapp.database.models.Vocabulary;
import com.nhom13.learningenglishapp.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class VocabularyDao {
    private DatabaseHelper dbHelper;
    private Context context;
    private ChapterDao chapterDao;

    public VocabularyDao(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
        chapterDao = new ChapterDao(context);
    }

    public boolean insertVocabulary(Vocabulary vocabulary, Uri imageUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Save image to local storage and get the path
            String imagePath = "";
            if (imageUri != null) {
                imagePath = FileUtils.saveImageToInternalStorage(context, imageUri,
                        vocabulary.getChapterId() + "_" + vocabulary.getWord());
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_VOCABULARY_WORD, vocabulary.getWord());
            values.put(DatabaseHelper.KEY_VOCABULARY_CHAPTER_ID, vocabulary.getChapterId());
            values.put(DatabaseHelper.KEY_VOCABULARY_IMAGE_PATH, imagePath);

            // Insert the new row
            long id = db.insert(DatabaseHelper.TABLE_VOCABULARY, null, values);
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

    public List<Vocabulary> getAllVocabulary() {
        List<Vocabulary> vocabularyList = new ArrayList<>();

        String VOCABULARY_SELECT_QUERY = "SELECT * FROM " + DatabaseHelper.TABLE_VOCABULARY;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(VOCABULARY_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Vocabulary vocabulary = new Vocabulary();
                    vocabulary.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_ID)));
                    vocabulary.setWord(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_WORD)));
                    vocabulary.setChapterId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_CHAPTER_ID)));
                    vocabulary.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_IMAGE_PATH)));

                    // Get the corresponding chapter
                    Chapter chapter = chapterDao.getChapterById(vocabulary.getChapterId());
                    vocabulary.setChapter(chapter);

                    vocabularyList.add(vocabulary);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return vocabularyList;
    }

    public List<Vocabulary> getVocabularyByChapter(int chapterId) {
        List<Vocabulary> vocabularyList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_VOCABULARY +
                " WHERE " + DatabaseHelper.KEY_VOCABULARY_CHAPTER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(chapterId)});

        try {
            if (cursor.moveToFirst()) {
                do {
                    Vocabulary vocabulary = new Vocabulary();
                    vocabulary.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_ID)));
                    vocabulary.setWord(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_WORD)));
                    vocabulary.setChapterId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_CHAPTER_ID)));
                    vocabulary.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_IMAGE_PATH)));

                    // Get the corresponding chapter
                    Chapter chapter = chapterDao.getChapterById(chapterId);
                    vocabulary.setChapter(chapter);

                    vocabularyList.add(vocabulary);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return vocabularyList;
    }

    public Vocabulary getVocabularyById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_VOCABULARY +
                " WHERE " + DatabaseHelper.KEY_VOCABULARY_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Vocabulary vocabulary = null;
        try {
            if (cursor.moveToFirst()) {
                vocabulary = new Vocabulary();
                vocabulary.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_ID)));
                vocabulary.setWord(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_WORD)));
                vocabulary.setChapterId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_CHAPTER_ID)));
                vocabulary.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_VOCABULARY_IMAGE_PATH)));

                // Get the corresponding chapter
                Chapter chapter = chapterDao.getChapterById(vocabulary.getChapterId());
                vocabulary.setChapter(chapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return vocabulary;
    }

    public boolean updateVocabulary(Vocabulary vocabulary, Uri newImageUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_VOCABULARY_WORD, vocabulary.getWord());
            values.put(DatabaseHelper.KEY_VOCABULARY_CHAPTER_ID, vocabulary.getChapterId());

            // Update image if a new one is provided
            if (newImageUri != null) {
                String imagePath = FileUtils.saveImageToInternalStorage(context, newImageUri,
                        vocabulary.getChapterId() + "_" + vocabulary.getWord());
                values.put(DatabaseHelper.KEY_VOCABULARY_IMAGE_PATH, imagePath);
            }

            // Updating row
            int result = db.update(DatabaseHelper.TABLE_VOCABULARY, values,
                    DatabaseHelper.KEY_VOCABULARY_ID + " = ?",
                    new String[]{String.valueOf(vocabulary.getId())});

            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVocabulary(int vocabularyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // First get the alphabet to retrieve the image path
        Vocabulary vocabulary = getVocabularyById(vocabularyId);

        // Delete the alphabet from database
        boolean success = db.delete(DatabaseHelper.TABLE_VOCABULARY,
                DatabaseHelper.KEY_VOCABULARY_ID + " = ?",
                new String[]{String.valueOf(vocabularyId)}) > 0;

        // If successful and there's an image, delete it from storage
        if (success && vocabulary != null && vocabulary.getImagePath() != null && !vocabulary.getImagePath().isEmpty()) {
            FileUtils.deleteImageFromInternalStorage(context, vocabulary.getImagePath());
        }

        return success;
    }

    public boolean deleteVocabularyByChapter(int chapterId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // First get all alphabet items of this chapter to delete their images
        List<Vocabulary> vocabularyList = getVocabularyByChapter(chapterId);

        // Delete all alphabet items of this chapter from database
        boolean success = db.delete(DatabaseHelper.TABLE_VOCABULARY,
                DatabaseHelper.KEY_VOCABULARY_CHAPTER_ID + " = ?",
                new String[]{String.valueOf(chapterId)}) > 0;

        // If successful delete all related images
        if (success) {
            for (Vocabulary vocabulary : vocabularyList) {
                if (vocabulary.getImagePath() != null && !vocabulary.getImagePath().isEmpty()) {
                    FileUtils.deleteImageFromInternalStorage(context, vocabulary.getImagePath());
                }
            }
        }

        return success;
    }

    // Trong class VocabularyDao

    // Lấy tổng số từ vựng
    public int getTotalVocabularyCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_VOCABULARY;
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    // (Tùy chọn) Lấy số lượng từ vựng theo chương
    public int getVocabularyCountByChapter(int chapterId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_VOCABULARY +
                " WHERE " + DatabaseHelper.KEY_VOCABULARY_CHAPTER_ID + " = ?";
        Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(chapterId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

}