package com.nhom13.learningenglishapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nhom13.learningenglishapp.database.DatabaseHelper;
import com.nhom13.learningenglishapp.database.models.QuizResult;

import java.util.ArrayList;
import java.util.List;

public class QuizResultDao {
    private DatabaseHelper dbHelper;
    private Context context;


    private static final String TABLE_QUIZ_RESULTS = "quiz_results";


    private static final String KEY_RESULT_ID = "id";
    private static final String KEY_RESULT_USER_ID = "user_id";
    private static final String KEY_RESULT_SCORE = "score";
    private static final String KEY_RESULT_TOTAL_QUESTIONS = "total_questions";
    private static final String KEY_RESULT_CORRECT_ANSWERS = "correct_answers";
    private static final String KEY_RESULT_DATE = "date";

    public QuizResultDao(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);


        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String CREATE_QUIZ_RESULTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_QUIZ_RESULTS +
                "(" +
                KEY_RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_RESULT_USER_ID + " INTEGER," +
                KEY_RESULT_SCORE + " INTEGER," +
                KEY_RESULT_TOTAL_QUESTIONS + " INTEGER," +
                KEY_RESULT_CORRECT_ANSWERS + " INTEGER," +
                KEY_RESULT_DATE + " INTEGER," +
                "FOREIGN KEY (" + KEY_RESULT_USER_ID + ") REFERENCES " +
                DatabaseHelper.TABLE_USERS + "(" + DatabaseHelper.KEY_USER_ID + ")" +
                ")";

        db.execSQL(CREATE_QUIZ_RESULTS_TABLE);
    }

    public boolean insertQuizResult(QuizResult result) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_RESULT_USER_ID, result.getUserId());
            values.put(KEY_RESULT_SCORE, result.getScore());
            values.put(KEY_RESULT_TOTAL_QUESTIONS, result.getTotalQuestions());
            values.put(KEY_RESULT_CORRECT_ANSWERS, result.getCorrectAnswers());
            values.put(KEY_RESULT_DATE, result.getDate());


            long id = db.insert(TABLE_QUIZ_RESULTS, null, values);
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

    public List<QuizResult> getQuizResultsByUser(int userId) {
        List<QuizResult> results = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_QUIZ_RESULTS +
                " WHERE " + KEY_RESULT_USER_ID + " = ?" +
                " ORDER BY " + KEY_RESULT_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        try {
            if (cursor.moveToFirst()) {
                do {
                    QuizResult result = new QuizResult();
                    result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_ID)));
                    result.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_USER_ID)));
                    result.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_SCORE)));
                    result.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_TOTAL_QUESTIONS)));
                    result.setCorrectAnswers(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_CORRECT_ANSWERS)));
                    result.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_RESULT_DATE)));

                    results.add(result);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return results;
    }

    public int getUserHighScore(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT MAX(" + KEY_RESULT_SCORE + ") FROM " + TABLE_QUIZ_RESULTS +
                " WHERE " + KEY_RESULT_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        int highScore = 0;
        try {
            if (cursor.moveToFirst()) {
                highScore = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return highScore;
    }

    public double getUserAverageScore(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT AVG(" + KEY_RESULT_SCORE + ") FROM " + TABLE_QUIZ_RESULTS +
                " WHERE " + KEY_RESULT_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        double averageScore = 0;
        try {
            if (cursor.moveToFirst()) {
                averageScore = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return averageScore;
    }

    public List<QuizResult> getTopScores(int limit) {
        List<QuizResult> results = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_QUIZ_RESULTS +
                " ORDER BY " + KEY_RESULT_SCORE + " DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    QuizResult result = new QuizResult();
                    result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_ID)));
                    result.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_USER_ID)));
                    result.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_SCORE)));
                    result.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_TOTAL_QUESTIONS)));
                    result.setCorrectAnswers(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RESULT_CORRECT_ANSWERS)));
                    result.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_RESULT_DATE)));

                    results.add(result);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return results;
    }

    public boolean deleteQuizResult(int resultId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(TABLE_QUIZ_RESULTS,
                KEY_RESULT_ID + " = ?",
                new String[]{String.valueOf(resultId)}) > 0;
    }

    public boolean deleteQuizResultsByUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(TABLE_QUIZ_RESULTS,
                KEY_RESULT_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}) > 0;
    }

    public int getTotalGamesPlayed() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_QUIZ_RESULTS;
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }


    public int getTotalQuestionsAttempted() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int total = 0;
        String sumQuery = "SELECT SUM(" + KEY_RESULT_TOTAL_QUESTIONS + ") FROM " + TABLE_QUIZ_RESULTS;
        Cursor cursor = db.rawQuery(sumQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }
            cursor.close();
        }
        return total;
    }


    public int getTotalCorrectAnswers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int total = 0;
        String sumQuery = "SELECT SUM(" + KEY_RESULT_CORRECT_ANSWERS + ") FROM " + TABLE_QUIZ_RESULTS;
        Cursor cursor = db.rawQuery(sumQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }
            cursor.close();
        }
        return total;
    }


    public double getAverageScore() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double average = 0;
        String avgQuery = "SELECT AVG(" + KEY_RESULT_SCORE + ") FROM " + TABLE_QUIZ_RESULTS;
        Cursor cursor = db.rawQuery(avgQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                average = cursor.getDouble(0);
            }
            cursor.close();
        }
        return average;
    }


}