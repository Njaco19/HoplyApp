package com.example.hoplyapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A fragment for the user to create a new post.
 */
public class CreatePost extends Fragment {

    //Empty public constructor required in a fragment.
    public CreatePost() { }

    /*
     * Uses ForkJoinPool to insert a new post into the remote and local database.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        ForkJoinPool fjp = new ForkJoinPool();

        EditText postText = view.findViewById(R.id.PostText);
        Button postButton = view.findViewById(R.id.PostButton);
        postButton.setOnClickListener((view1) -> {
            Random rand = new Random();
            Post post = new Post();
            String postTextString = postText.getText().toString(); // Get the text from the user.

            // Creates the post with a given text.
            post.id = rand.nextInt();
            post.content = postTextString;
            post.user_id = LoginScreen.thisUser.id;
            post.stamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()).format((DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            // Inserts the user into the remote database.
            Call<Post> insertPost = MainActivity.remoteDB.insertPost(post.id, post.user_id, post.content, post.stamp);
            insertPost.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(!response.isSuccessful()){ // If there was an error, this displays the error message.
                        Toast.makeText(CreatePost.this.getActivity(), response.code() + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {}
            });

            // Inserts the post into the local database and navigates back to the front page.
            fjp.execute(() -> MainActivity.ld.getDAO().insertPost(post));
            NavHostFragment.findNavController(CreatePost.this)
                    .navigate(R.id.action_createPost_to_FrontPage);
        });

        // Navigates back to the front page without creating a new post.
        Button cancel = view.findViewById(R.id.CancelPost);
        cancel.setOnClickListener((view1) -> {
            NavHostFragment.findNavController(CreatePost.this)
                    .navigate(R.id.action_createPost_to_FrontPage);
        });
        return view;
    }
}
