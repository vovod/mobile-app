package com.nhom13.learningenglishapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.nhom13.learningenglishapp.database.DatabaseHelper;
import com.nhom13.learningenglishapp.database.models.Quiz;
import com.nhom13.learningenglishapp.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class QuizDao {
    private DatabaseHelper dbHelper;
    private Context context;

    public QuizDao(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean insertQuiz(Quiz quiz, Uri imageUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Save image to local storage and get the path
            String imagePath = "";
            if (imageUri != null) {
                imagePath = FileUtils.saveImageToInternalStorage(context, imageUri, "quiz_" + System.currentTimeMillis());
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_QUIZ_CORRECT_ANSWER, quiz.getCorrectAnswer());
            values.put(DatabaseHelper.KEY_QUIZ_WRONG_ANSWER, quiz.getWrongAnswer());
            values.put(DatabaseHelper.KEY_QUIZ_IMAGE_PATH, imagePath);

            // Insert the new row
            long id = db.insert(DatabaseHelper.TABLE_QUIZ, null, values);
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

    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();

        String QUIZ_SELECT_QUERY = "SELECT * FROM " + DatabaseHelper.TABLE_QUIZ;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUIZ_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Quiz quiz = new Quiz();
                    quiz.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_ID)));
                    quiz.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_CORRECT_ANSWER)));
                    quiz.setWrongAnswer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_WRONG_ANSWER)));
                    quiz.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_IMAGE_PATH)));

                    quizzes.add(quiz);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return quizzes;
    }

    public Quiz getQuizById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_QUIZ +
                " WHERE " + DatabaseHelper.KEY_QUIZ_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Quiz quiz = null;
        try {
            if (cursor.moveToFirst()) {
                quiz = new Quiz();
                quiz.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_ID)));
                quiz.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_CORRECT_ANSWER)));
                quiz.setWrongAnswer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_WRONG_ANSWER)));
                quiz.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_IMAGE_PATH)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return quiz;
    }

    public boolean updateQuiz(Quiz quiz, Uri newImageUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_QUIZ_CORRECT_ANSWER, quiz.getCorrectAnswer());
            values.put(DatabaseHelper.KEY_QUIZ_WRONG_ANSWER, quiz.getWrongAnswer());

            // Update image if a new one is provided
            if (newImageUri != null) {
                String imagePath = FileUtils.saveImageToInternalStorage(context, newImageUri,
                        "quiz_update_" + System.currentTimeMillis());
                values.put(DatabaseHelper.KEY_QUIZ_IMAGE_PATH, imagePath);

                // Delete old image if it exists
                if (quiz.getImagePath() != null && !quiz.getImagePath().isEmpty()) {
                    FileUtils.deleteImageFromInternalStorage(context, quiz.getImagePath());
                }
            }

            // Updating row
            int result = db.update(DatabaseHelper.TABLE_QUIZ, values,
                    DatabaseHelper.KEY_QUIZ_ID + " = ?",
                    new String[]{String.valueOf(quiz.getId())});

            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuiz(int quizId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // First get the quiz to retrieve the image path
        Quiz quiz = getQuizById(quizId);

        // Delete the quiz from database
        boolean success = db.delete(DatabaseHelper.TABLE_QUIZ,
                DatabaseHelper.KEY_QUIZ_ID + " = ?",
                new String[]{String.valueOf(quizId)}) > 0;

        // If successful and there's an image, delete it from storage
        if (success && quiz != null && quiz.getImagePath() != null && !quiz.getImagePath().isEmpty()) {
            FileUtils.deleteImageFromInternalStorage(context, quiz.getImagePath());
        }

        return success;
    }

    // Thêm phương thức để lấy số lượng quiz
    public int getQuizCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_QUIZ;
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }

        return count;
    }

    // Phương thức để lấy quiz ngẫu nhiên
    public Quiz getRandomQuiz() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_QUIZ + " ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        Quiz quiz = null;
        try {
            if (cursor.moveToFirst()) {
                quiz = new Quiz();
                quiz.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_ID)));
                quiz.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_CORRECT_ANSWER)));
                quiz.setWrongAnswer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_WRONG_ANSWER)));
                quiz.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_IMAGE_PATH)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return quiz;
    }

    // Phương thức để lấy một số lượng quiz ngẫu nhiên
    public List<Quiz> getRandomQuizzes(int limit) {
        List<Quiz> quizzes = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_QUIZ + " ORDER BY RANDOM() LIMIT " + limit;
        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Quiz quiz = new Quiz();
                    quiz.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_ID)));
                    quiz.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_CORRECT_ANSWER)));
                    quiz.setWrongAnswer(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_WRONG_ANSWER)));
                    quiz.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_QUIZ_IMAGE_PATH)));

                    quizzes.add(quiz);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return quizzes;
    }
}