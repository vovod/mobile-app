package com.nhom13.learningenglishapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "EnglishLearningDB";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CHAPTERS = "chapters";
    public static final String TABLE_VOCABULARY = "vocabulary";
    public static final String TABLE_QUIZ = "quiz";
    public static final String TABLE_VIDEOS = "videos";

    // User Table Columns
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_USERNAME = "username";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_SCORE = "score";

    // Chapter Table Columns
    public static final String KEY_CHAPTER_ID = "id";
    public static final String KEY_CHAPTER_NAME = "name";
    public static final String KEY_CHAPTER_IMAGE_PATH = "image_path";

    // Vocabulary Table Columns
    public static final String KEY_VOCABULARY_ID = "id";
    public static final String KEY_VOCABULARY_WORD = "word";
    public static final String KEY_VOCABULARY_CHAPTER_ID = "chapter_id";
    public static final String KEY_VOCABULARY_IMAGE_PATH = "image_path";

    // Quiz Table Columns
    public static final String KEY_QUIZ_ID = "id";
    public static final String KEY_QUIZ_CORRECT_ANSWER = "correct_answer";
    public static final String KEY_QUIZ_WRONG_ANSWER = "wrong_answer";
    public static final String KEY_QUIZ_IMAGE_PATH = "image_path";

    // Video Table Columns
    public static final String KEY_VIDEO_ID = "id";
    public static final String KEY_VIDEO_TITLE = "title";
    public static final String KEY_VIDEO_THUMBNAIL = "thumbnail";
    public static final String KEY_VIDEO_URL = "video_url";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_USER_USERNAME + " TEXT UNIQUE," +
                KEY_USER_PASSWORD + " TEXT," +
                KEY_USER_SCORE + " INTEGER DEFAULT 0" +
                ")";

        String CREATE_CHAPTERS_TABLE = "CREATE TABLE " + TABLE_CHAPTERS +
                "(" +
                KEY_CHAPTER_ID + " INTEGER PRIMARY KEY," +
                KEY_CHAPTER_NAME + " TEXT," +
                KEY_CHAPTER_IMAGE_PATH + " TEXT" +
                ")";

        String CREATE_VOCABULARY_TABLE = "CREATE TABLE " + TABLE_VOCABULARY +
                "(" +
                KEY_VOCABULARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_VOCABULARY_WORD + " TEXT," +
                KEY_VOCABULARY_CHAPTER_ID + " INTEGER," +
                KEY_VOCABULARY_IMAGE_PATH + " TEXT," +
                "FOREIGN KEY (" + KEY_VOCABULARY_CHAPTER_ID + ") REFERENCES " + TABLE_CHAPTERS + "(" + KEY_CHAPTER_ID + ")" +
                ")";

        String CREATE_QUIZ_TABLE = "CREATE TABLE " + TABLE_QUIZ +
                "(" +
                KEY_QUIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_QUIZ_CORRECT_ANSWER + " TEXT," +
                KEY_QUIZ_WRONG_ANSWER + " TEXT," +
                KEY_QUIZ_IMAGE_PATH + " TEXT" +
                ")";

        String CREATE_VIDEOS_TABLE = "CREATE TABLE " + TABLE_VIDEOS +
                "(" +
                KEY_VIDEO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_VIDEO_TITLE + " TEXT," +
                KEY_VIDEO_THUMBNAIL + " TEXT," +
                KEY_VIDEO_URL + " TEXT" +
                ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CHAPTERS_TABLE);
        db.execSQL(CREATE_VOCABULARY_TABLE);
        db.execSQL(CREATE_QUIZ_TABLE);
        db.execSQL(CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Drop older tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCABULARY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

            // Recreate tables
            onCreate(db);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}