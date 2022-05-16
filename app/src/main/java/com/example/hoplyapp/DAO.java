package com.example.hoplyapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Interface that defines methods to interact with our local database.
 */
@Dao
public interface DAO {

    // Orders the posts from newest to oldest
    @Query("SELECT * FROM posts ORDER BY stamp DESC")
    List<Post> getAllPost();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPost(Post post);

    @Query("SELECT * FROM comments")
    List<Comment> getAllComment();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertComment(Comment comment);

    @Query("SELECT id FROM users")
    List<String> getAllUserID();

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User user);

    @Update
    void updateUser(User user);
}

