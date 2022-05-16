package com.example.hoplyapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Fragment used to see a specific post, and comment on that post.
 * This fragment uses scroll views to show the entire post and all comments.
 */

public class PostView extends Fragment {

    //Empty public constructor required in a fragment.
    public PostView() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_view, container, false);
        ForkJoinPool fjp = new ForkJoinPool();
        LinearLayout commentFeed = view.findViewById(R.id.commentFeed);

        try {

            //Gets all users and comments from the local database.
            FutureTask<List<Comment>> ftComment = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllComment());
            FutureTask<List<User>> ftUser = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllUsers());
            fjp.execute(ftComment);
            fjp.execute(ftUser);
            List<Comment> comments = ftComment.get();
            List<User> users = ftUser.get();

            TextView post = view.findViewById(R.id.currentPost);
            Post p = FrontPage.currentPost;


            User thisUser = users.stream().filter(u -> p.user_id.equals(u.id)).findAny().get(); // Ensures that the name of the user is shown instead of their id.
            if(thisUser.name.length() >= 100)
                thisUser.name = thisUser.name.substring(0,100);
            post.setText("\n Posted by: " + thisUser.name + "\n \n " + p.content + "\n \n Posted at: " + p.stamp + "\n " ); // Shows the post

            // Creates a text view for every comment, then adding that text view to the scroll view.
            for (Comment c : comments) {
                if (c.post_id == p.id) {
                    TextView comment = new TextView(PostView.this.getActivity());
                    User commentUser = users.stream().filter(u -> c.user_id.equals(u.id)).findAny().get();
                    if(commentUser.name.length() >= 100)
                        commentUser.name = commentUser.name.substring(0,100);
                    comment.setText(" Posted by: " + commentUser.name + "\n \n " + c.content + "\n Posted at: " + c.stamp +"\n");
                    commentFeed.addView(comment);
                }
            }

            // Adds the users comment to the remote and local database, when the user clicks on the post button.
            Button postBtn = view.findViewById(R.id.PostBtn);
            postBtn.setOnClickListener((view1) -> {
                Comment currentComment = new Comment();
                EditText contentEdit = view.findViewById(R.id.commentText);
                String content = contentEdit.getText().toString();

                // Creates the comment with a given text.
                currentComment.content = content;
                currentComment.user_id = LoginScreen.thisUser.id; // The logged in user's id.
                currentComment.stamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()),
                        ZoneId.systemDefault()).format((DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                currentComment.post_id = p.id;

                fjp.execute(() -> MainActivity.ld.getDAO().insertComment(currentComment)); // Inserts the comment into the local database

                // Inserts the comment into the remote database.
                Call<Comment> insertComment = MainActivity.remoteDB.insertComment(currentComment.user_id, currentComment.post_id, currentComment.content, currentComment.stamp);
                insertComment.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        if(!response.isSuccessful()){ // If there was an error, this displays the error message
                            Toast.makeText(PostView.this.getActivity(), response.code() + response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {}
                });

                // Refreshes the fragment.
                NavHostFragment.findNavController(PostView.this)
                        .navigate(R.id.action_postView_self);
            });
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }

        // Navigates back to the front page.
        Button back = view.findViewById(R.id.BackBtn);
        back.setOnClickListener((view1) -> {
            NavHostFragment.findNavController(PostView.this)
                    .navigate(R.id.action_postView_to_FrontPage);
        });

        return view;
    }
}
