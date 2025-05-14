package com.nhom13.learningenglishapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nhom13.learningenglishapp.database.DatabaseHelper;
import com.nhom13.learningenglishapp.database.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private DatabaseHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean createUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_USER_USERNAME, user.getUsername());
            values.put(DatabaseHelper.KEY_USER_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.KEY_USER_SCORE, user.getScore());

            // Insert the new row, returning the primary key value of the new row
            long id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
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

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        // SELECT * FROM users
        String USERS_SELECT_QUERY = "SELECT * FROM " + DatabaseHelper.TABLE_USERS;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(USERS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_ID)));
                    user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_USERNAME)));
                    user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_PASSWORD)));
                    user.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_SCORE)));

                    users.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return users;
    }

    public List<User> getAllNonAdminUsers() {
        List<User> users = new ArrayList<>();

        // SELECT * FROM users WHERE username != 'admin'
        String USERS_SELECT_QUERY = "SELECT * FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE " + DatabaseHelper.KEY_USER_USERNAME + " != 'admin'";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(USERS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_ID)));
                    user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_USERNAME)));
                    user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_PASSWORD)));
                    user.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_SCORE)));

                    users.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return users;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE " + DatabaseHelper.KEY_USER_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

        User user = null;
        try {
            if (cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_PASSWORD)));
                user.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_SCORE)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return user;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE " + DatabaseHelper.KEY_USER_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

        try {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count == 0; // Return true if username doesn't exist
            }
            return true;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public boolean updateScore(String username, int score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_USER_SCORE, score);

        // Updating row
        int result = db.update(DatabaseHelper.TABLE_USERS, values,
                DatabaseHelper.KEY_USER_USERNAME + " = ?",
                new String[]{username});

        return result > 0;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.delete(DatabaseHelper.TABLE_USERS,
                DatabaseHelper.KEY_USER_USERNAME + " = ?",
                new String[]{username}) > 0;
    }

    // Trong class UserDao

    // Lấy tổng số người dùng (không bao gồm admin)
    public int getTotalUserCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE " + DatabaseHelper.KEY_USER_USERNAME + " != 'admin'";
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        // Không đóng db ở đây vì dbHelper quản lý lifecycle của db
        return count;
    }

    // Lấy danh sách người dùng theo điểm giảm dần (Top N users)
    public List<User> getTopUsersByScore(int limit) {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE " + DatabaseHelper.KEY_USER_USERNAME + " != 'admin'" +
                " ORDER BY " + DatabaseHelper.KEY_USER_SCORE + " DESC" +
                " LIMIT " + limit;

        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_ID)));
                    user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_USERNAME)));
                    user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_PASSWORD)));
                    user.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_SCORE)));
                    users.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        // Không đóng db ở đây
        return users;
    }

// Cần đảm bảo phương thức `getAllNonAdminUsers()` đã có trong file của bạn, nó cũng sẽ hữu ích.
// Nếu chưa có, hãy thêm nó. Dựa trên tệp bạn gửi, nó đã có rồi.
}