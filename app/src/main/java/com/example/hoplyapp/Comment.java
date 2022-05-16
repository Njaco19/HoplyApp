package com.example.hoplyapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Declares the schema for comments in the local database.
 */

//Primary key declarations
@Entity(tableName = "comments",
        primaryKeys = {"user_id", "post_id", "stamp" })
public class Comment {
    @NonNull
    @ColumnInfo(name = "user_id")
    public String user_id;

    @NonNull
    @ColumnInfo(name = "post_id")
    public int post_id;

    @ColumnInfo(name = "content")
    public String content;

    @NonNull
    @ColumnInfo(name = "stamp")
    public String stamp;
}
