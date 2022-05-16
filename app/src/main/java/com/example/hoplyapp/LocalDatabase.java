package com.example.hoplyapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Abstract class for the local database.
 * Makes it possible to create an instance of the local database.
 * Used by Android Room.
 */
@Database(entities = {User.class, Comment.class, Post.class}, version = 2, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract DAO getDAO();
}
