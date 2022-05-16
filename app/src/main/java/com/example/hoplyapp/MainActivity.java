package com.example.hoplyapp;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * This class is the main activity for the entire app and is run when the app is opened.
 */
public class MainActivity extends AppCompatActivity {
    public static LocalDatabase ld;
    public static RemoteDB remoteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Creates a static instance of the local database, so it can be accessed from other fragments.
        ld = Room.databaseBuilder(getApplicationContext(), LocalDatabase.class, "HoplyDB").fallbackToDestructiveMigration().build();

        // Creates a static instance of the remote database, so it can be accessed from other fragments.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://caracal.imada.sdu.dk/app2020/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        remoteDB = retrofit.create(RemoteDB.class);

        ForkJoinPool fjp = new ForkJoinPool();

        // Gets all users from the remote database, and inserts them into the local database.
        Call<List<User>> userCall = remoteDB.getUsers();
        userCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                List<User> users = response.body();
                try {
                    FutureTask<List<User>> ftUsers = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllUsers());
                    fjp.execute(ftUsers);
                    List<User> currentUsers = ftUsers.get();
                    for (User u : users) {
                        if (!currentUsers.contains(u)) // Checks if the user is already in the database, so it doesn't insert it twice.
                            fjp.execute(() -> MainActivity.ld.getDAO().insertUser(u));
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (ExecutionException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Gets all posts from the remote database, and inserts them into the local database.
        Call<List<Post>> postCall = remoteDB.getPosts();
        postCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                List<Post> posts = response.body();
                try {
                    FutureTask<List<Post>> ftPosts = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllPost());
                    fjp.execute(ftPosts);
                    List<Post> currentPosts = ftPosts.get();
                    for (Post p : posts) {
                        if (!currentPosts.contains(p) && p.user_id != null && p.content.length() < 1000) { // Checks if the post is already in the database, so it doesn't insert it twice.
                            fjp.execute(() -> MainActivity.ld.getDAO().insertPost(p));
                        }
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }catch(ExecutionException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Gets all comments from the remote database, and inserts them into the local database.
        Call<List<Comment>> commentCall = remoteDB.getComments();
        commentCall.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                List<Comment> comments = response.body();
                try {
                    FutureTask<List<Comment>> ftComment = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllComment());
                    fjp.execute(ftComment);
                    List<Comment> currentComments = ftComment.get();
                    for (Comment c : comments) {
                        if (!currentComments.contains(c))// Checks if the comment is already in the database, so it doesn't insert it twice.
                            fjp.execute(() -> MainActivity.ld.getDAO().insertComment(c));
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (ExecutionException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
