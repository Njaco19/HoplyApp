package com.example.hoplyapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Declares the schema for posts in the local database.
 */

@Entity(tableName = "posts")
public class Post {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "user_id")
    public String user_id;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "stamp")
    public String stamp;
}
