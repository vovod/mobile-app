package com.nhom13.learningenglishapp.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nhom13.learningenglishapp.database.DatabaseHelper;
import com.nhom13.learningenglishapp.database.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private DatabaseHelper dbHelper;
    private static final String TAG = "UserDao";

    public UserDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public boolean createUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.KEY_USER_USERNAME, user.getUsername());
            values.put(DatabaseHelper.KEY_USER_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.KEY_USER_SCORE, user.getScore());


            id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
            if (id != -1) {
                Log.d(TAG, "Created user: " + user.getUsername() + " with id: " + id);
            } else {
                Log.e(TAG, "Failed to create user: " + user.getUsername());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating user: " + user.getUsername(), e);
        } finally {

        }
        return id != -1;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
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
            Log.e(TAG, "Error getting all users", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return users;
    }

    public List<User> getAllNonAdminUsers() {
        List<User> users = new ArrayList<>();
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
            Log.e(TAG, "Error getting all non-admin users", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return users;
    }


    public User getUserById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE " + DatabaseHelper.KEY_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

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
            Log.e(TAG, "Error getting user by ID: " + id, e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return user;
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
            Log.e(TAG, "Error getting user by username: " + username, e);
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
                return count == 0;
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

        int result = 0;
        try {
            // Updating row
            result = db.update(DatabaseHelper.TABLE_USERS, values,
                    DatabaseHelper.KEY_USER_USERNAME + " = ?",
                    new String[]{username});
            if (result > 0) {
                Log.d(TAG, "Updated score for user: " + username + " to " + score);
            } else {
                Log.w(TAG, "Score update failed for user: " + username + " (user not found or no changes)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating score for user: " + username, e);
        } finally {

        }
        return result > 0;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;
        try {
            result = db.delete(DatabaseHelper.TABLE_USERS,
                    DatabaseHelper.KEY_USER_USERNAME + " = ?",
                    new String[]{username});
            if (result > 0) {
                Log.d(TAG, "Deleted user: " + username);
            } else {
                Log.w(TAG, "User deletion failed: " + username + " (user not found)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting user: " + username, e);
        } finally {

        }
        return result > 0;
    }


    public int getTotalUserCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS +
                " WHERE " + DatabaseHelper.KEY_USER_USERNAME + " != 'admin'";
        Cursor cursor = db.rawQuery(countQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting total user count", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return count;
    }


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
            Log.e(TAG, "Error getting top users by score", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return users;
    }


    public int getUserCountByScoreRange(int minScore, int maxScore) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        String countQuery;
        String[] selectionArgs;


        if (maxScore == Integer.MAX_VALUE) {
            countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS +
                    " WHERE " + DatabaseHelper.KEY_USER_SCORE + " >= ?" +
                    " AND " + DatabaseHelper.KEY_USER_USERNAME + " != 'admin'";
            selectionArgs = new String[]{String.valueOf(minScore)};
        } else {
            countQuery = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS +
                    " WHERE " + DatabaseHelper.KEY_USER_SCORE + " >= ? AND " +
                    DatabaseHelper.KEY_USER_SCORE + " <= ?" +
                    " AND " + DatabaseHelper.KEY_USER_USERNAME + " != 'admin'";
            selectionArgs = new String[]{String.valueOf(minScore), String.valueOf(maxScore)};
        }


        Cursor cursor = db.rawQuery(countQuery, selectionArgs);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user count by score range", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return count;
    }



}
