package com.nhom13.learningenglishapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "EnglishLearningDB";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CHAPTERS = "chapters";
    public static final String TABLE_VOCABULARY = "vocabulary";
    public static final String TABLE_QUIZ = "quiz";
    public static final String TABLE_VIDEOS = "videos";
    public static final String TABLE_QUIZ_RESULTS = "quiz_results";

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

    // Quiz Results Table Columns
    public static final String KEY_RESULT_ID = "id";
    public static final String KEY_RESULT_USER_ID = "user_id";
    public static final String KEY_RESULT_SCORE = "score";
    public static final String KEY_RESULT_TOTAL_QUESTIONS = "total_questions";
    public static final String KEY_RESULT_CORRECT_ANSWERS = "correct_answers";
    public static final String KEY_RESULT_DATE = "date";

    private static DatabaseHelper instance;
    private Context context;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");

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

        String CREATE_QUIZ_RESULTS_TABLE = "CREATE TABLE " + TABLE_QUIZ_RESULTS +
                "(" +
                KEY_RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_RESULT_USER_ID + " INTEGER," +
                KEY_RESULT_SCORE + " INTEGER," +
                KEY_RESULT_TOTAL_QUESTIONS + " INTEGER," +
                KEY_RESULT_CORRECT_ANSWERS + " INTEGER," +
                KEY_RESULT_DATE + " INTEGER," +
                "FOREIGN KEY (" + KEY_RESULT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")" +
                ")";

        try {
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_CHAPTERS_TABLE);
            db.execSQL(CREATE_VOCABULARY_TABLE);
            db.execSQL(CREATE_QUIZ_TABLE);
            db.execSQL(CREATE_VIDEOS_TABLE);
            db.execSQL(CREATE_QUIZ_RESULTS_TABLE);

            Log.d(TAG, "All tables created successfully");

            // Luôn luôn chèn dữ liệu mẫu khi tạo database mới
            insertSampleData(db);

        } catch (Exception e) {
            Log.e(TAG, "Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion != newVersion) {
            // Drop tables in correct order (considering foreign keys)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCABULARY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private boolean isDatabaseEmpty(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CHAPTERS, null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Chapters count: " + count);
                return count == 0;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if database is empty", e);
            return true;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void insertSampleData(SQLiteDatabase db) {
        Log.d(TAG, "Starting to insert sample data");

        // Sử dụng transaction để đảm bảo tính nhất quán
        db.beginTransaction();
        try {
            // Kiểm tra xem database có trống không
            if (!isDatabaseEmpty(db)) {
                Log.d(TAG, "Database is not empty, skipping sample data insertion");
                db.setTransactionSuccessful();
                return;
            }

            // Chèn dữ liệu mẫu cho chapters, vocabulary, quiz từ file SQL
            boolean dataInserted = false;

            // Thử đọc từ file SQL trước
            if (insertDataFromSQLFile(db, "sample_data.sql")) {
                Log.d(TAG, "Successfully inserted data from sample_data.sql");
                dataInserted = true;
            }

            if (insertDataFromSQLFile(db, "sample_videos.sql")) {
                Log.d(TAG, "Successfully inserted data from sample_videos.sql");
                dataInserted = true;
            }

            // Nếu không thể đọc từ file SQL, chèn dữ liệu hardcode
            if (!dataInserted) {
                Log.d(TAG, "Inserting hardcoded sample data");
                insertHardcodedSampleData(db);
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "Sample data insertion completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error inserting sample data", e);
        } finally {
            db.endTransaction();
        }
    }

    private boolean insertDataFromSQLFile(SQLiteDatabase db, String fileName) {
        try {
            Log.d(TAG, "Trying to read from file: " + fileName);

            // Kiểm tra xem file có tồn tại không
            String[] assetList = context.getAssets().list("");
            boolean fileExists = false;
            for (String asset : assetList) {
                if (asset.equals(fileName)) {
                    fileExists = true;
                    break;
                }
            }

            if (!fileExists) {
                Log.w(TAG, "File " + fileName + " not found in assets");
                return false;
            }

            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            int lineCount = 0;
            int executedCount = 0;

            while ((line = reader.readLine()) != null) {
                lineCount++;
                line = line.trim();

                // Bỏ qua comment và dòng trống
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) {
                    continue;
                }

                sqlBuilder.append(line).append(" ");

                // Thực thi câu lệnh khi gặp dấu ;
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();
                    if (!sql.isEmpty()) {
                        try {
                            db.execSQL(sql);
                            executedCount++;
                            Log.d(TAG, "Executed SQL: " + sql.substring(0, Math.min(sql.length(), 100)) + "...");
                        } catch (Exception e) {
                            Log.e(TAG, "Error executing SQL: " + sql, e);
                        }
                    }
                    sqlBuilder.setLength(0);
                }
            }

            reader.close();
            inputStream.close();

            Log.d(TAG, "Processed " + lineCount + " lines, executed " + executedCount + " statements from " + fileName);
            return executedCount > 0;

        } catch (IOException e) {
            Log.e(TAG, "IOException reading " + fileName, e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error processing " + fileName, e);
            return false;
        }
    }

    private void insertHardcodedSampleData(SQLiteDatabase db) {
        try {
            // Insert sample chapters
            db.execSQL("INSERT INTO " + TABLE_CHAPTERS + " (" + KEY_CHAPTER_ID + ", " + KEY_CHAPTER_NAME + ", " + KEY_CHAPTER_IMAGE_PATH + ") VALUES (1, 'Basic Vocabulary', 'chapter1.png')");
            db.execSQL("INSERT INTO " + TABLE_CHAPTERS + " (" + KEY_CHAPTER_ID + ", " + KEY_CHAPTER_NAME + ", " + KEY_CHAPTER_IMAGE_PATH + ") VALUES (2, 'Family & Relationships', 'chapter2.png')");
            db.execSQL("INSERT INTO " + TABLE_CHAPTERS + " (" + KEY_CHAPTER_ID + ", " + KEY_CHAPTER_NAME + ", " + KEY_CHAPTER_IMAGE_PATH + ") VALUES (3, 'Food & Drinks', 'chapter3.png')");

            // Insert sample vocabulary
            db.execSQL("INSERT INTO " + TABLE_VOCABULARY + " (" + KEY_VOCABULARY_WORD + ", " + KEY_VOCABULARY_CHAPTER_ID + ", " + KEY_VOCABULARY_IMAGE_PATH + ") VALUES ('Hello', 1, 'hello.png')");
            db.execSQL("INSERT INTO " + TABLE_VOCABULARY + " (" + KEY_VOCABULARY_WORD + ", " + KEY_VOCABULARY_CHAPTER_ID + ", " + KEY_VOCABULARY_IMAGE_PATH + ") VALUES ('Goodbye', 1, 'goodbye.png')");
            db.execSQL("INSERT INTO " + TABLE_VOCABULARY + " (" + KEY_VOCABULARY_WORD + ", " + KEY_VOCABULARY_CHAPTER_ID + ", " + KEY_VOCABULARY_IMAGE_PATH + ") VALUES ('Thank you', 1, 'thankyou.png')");

            db.execSQL("INSERT INTO " + TABLE_VOCABULARY + " (" + KEY_VOCABULARY_WORD + ", " + KEY_VOCABULARY_CHAPTER_ID + ", " + KEY_VOCABULARY_IMAGE_PATH + ") VALUES ('Father', 2, 'father.png')");
            db.execSQL("INSERT INTO " + TABLE_VOCABULARY + " (" + KEY_VOCABULARY_WORD + ", " + KEY_VOCABULARY_CHAPTER_ID + ", " + KEY_VOCABULARY_IMAGE_PATH + ") VALUES ('Mother', 2, 'mother.png')");
            db.execSQL("INSERT INTO " + TABLE_VOCABULARY + " (" + KEY_VOCABULARY_WORD + ", " + KEY_VOCABULARY_CHAPTER_ID + ", " + KEY_VOCABULARY_IMAGE_PATH + ") VALUES ('Brother', 2, 'brother.png')");

            // Insert sample quiz questions
            db.execSQL("INSERT INTO " + TABLE_QUIZ + " (" + KEY_QUIZ_CORRECT_ANSWER + ", " + KEY_QUIZ_WRONG_ANSWER + ", " + KEY_QUIZ_IMAGE_PATH + ") VALUES ('Hello', 'Goodbye', 'hello_quiz.png')");
            db.execSQL("INSERT INTO " + TABLE_QUIZ + " (" + KEY_QUIZ_CORRECT_ANSWER + ", " + KEY_QUIZ_WRONG_ANSWER + ", " + KEY_QUIZ_IMAGE_PATH + ") VALUES ('Father', 'Mother', 'father_quiz.png')");

            // Insert sample videos
            db.execSQL("INSERT INTO " + TABLE_VIDEOS + " (" + KEY_VIDEO_TITLE + ", " + KEY_VIDEO_THUMBNAIL + ", " + KEY_VIDEO_URL + ") VALUES ('Basic English Greetings', 'greeting_thumb.png', 'https://example.com/greeting.mp4')");
            db.execSQL("INSERT INTO " + TABLE_VIDEOS + " (" + KEY_VIDEO_TITLE + ", " + KEY_VIDEO_THUMBNAIL + ", " + KEY_VIDEO_URL + ") VALUES ('Family Members', 'family_thumb.png', 'https://example.com/family.mp4')");

            Log.d(TAG, "Hardcoded sample data inserted successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error inserting hardcoded sample data", e);
            throw e;
        }
    }

    // Helper method để kiểm tra xem table có dữ liệu không
    public boolean hasData(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking data in table " + tableName, e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Method để force reload sample data (hữu ích cho testing)
    public void reloadSampleData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Clear existing data
            db.execSQL("DELETE FROM " + TABLE_QUIZ_RESULTS);
            db.execSQL("DELETE FROM " + TABLE_VOCABULARY);
            db.execSQL("DELETE FROM " + TABLE_QUIZ);
            db.execSQL("DELETE FROM " + TABLE_VIDEOS);
            db.execSQL("DELETE FROM " + TABLE_CHAPTERS);
            db.execSQL("DELETE FROM " + TABLE_USERS);

            // Insert sample data
            insertSampleData(db);

            db.setTransactionSuccessful();
            Log.d(TAG, "Sample data reloaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error reloading sample data", e);
        } finally {
            db.endTransaction();
        }
    }
}