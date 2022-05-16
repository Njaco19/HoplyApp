package com.example.hoplyapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.annotation.NonNull;

import androidx.navigation.fragment.NavHostFragment;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;

/**
 * This fragment is the front page of the app, where the user can see a feed of all posts in the JSON database.
 */
public class FrontPage extends Fragment {
    public static Post currentPost; //This is necessary so we can show the right post in the PostView.

    /*
     * This method adds three buttons to the bottom of the fragment.
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_front_page, container, false);

        // Navigates to the createPost fragment.
        FloatingActionButton toPosts = view.findViewById(R.id.PostButton);
        toPosts.setOnClickListener((view1) ->{
            NavHostFragment.findNavController(FrontPage.this)
                    .navigate(R.id.action_FrontPage_to_createPost);
        });

        // Navigates to the changeName fragment.
        Button updateUsername = view.findViewById(R.id.ChangeName);
        updateUsername.setOnClickListener((view1) -> {
            NavHostFragment.findNavController(FrontPage.this)
                    .navigate(R.id.action_FrontPage_to_changeName2);
        });

        // Sets the static variable thisUser to null, and navigates back to the MainScreen.
        Button logout = view.findViewById(R.id.Logout);
        logout.setOnClickListener((view1) -> {
            LoginScreen.thisUser = null;
            NavHostFragment.findNavController(FrontPage.this)
                    .navigate(R.id.action_FrontPage_to_MainScreen);
        });

        return view;
    }

    /*
     * Creates a scrollview with the name of this user at the top, followed by all posts.
     * If the user wants to see a post or add a comment, they can press "View" to navigate to the PostView fragment.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ForkJoinPool fjp = new ForkJoinPool();
        LinearLayout feed = view.findViewById(R.id.Feed);

        //Displays the name of this user.
        TextView username = new TextView(FrontPage.this.getActivity());
        username.setText(" Logged in as: " + LoginScreen.thisUser.name);
        feed.addView(username);

        try {
            FutureTask<List<User>> ftUser = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllUsers());
            FutureTask<List<Post>> ftPost = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllPost());
            fjp.execute(ftUser);
            fjp.execute(ftPost);
            List<User> users = ftUser.get();
            List<Post> posts = ftPost.get();

            for (Post p : posts) {
                TextView textTrue = new TextView(FrontPage.this.getActivity());
                String cont = p.content;
                // If a post is super long, we don't want it to take up the entire front page, so here we limit the number of characters shown.
                if(p.content.length() >= 100)
                    cont = p.content.substring(0,100) + " ...";
                User thisUser = users.stream().filter(u -> p.user_id.equals(u.id)).findAny().get();
                // Some users have super long usernames. To keep them from taking up the entire front page, we limit them to 100 characters.
                if(thisUser.name.length() >= 100)
                    thisUser.name = thisUser.name.substring(0,100);
                textTrue.setText("\n Posted by: " + thisUser.name + "\n \n " + cont + "\n \n Posted at: " + p.stamp + "\n " );
                feed.addView(textTrue);

                // Creates a new button for every post, that navigates to the PostView fragment showing this post.
                Button seePost = new Button(FrontPage.this.getActivity());
                seePost.setText("View");
                seePost.setOnClickListener((view1) -> {
                    currentPost = p;
                    NavHostFragment.findNavController(FrontPage.this)
                            .navigate(R.id.action_FrontPage_to_postView);
                });
                feed.addView(seePost);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
            System.out.println("This is a fail message \n \n \n \n \n");
        }catch (ExecutionException e){
            e.printStackTrace();
            System.out.println("This is a fail message \n \n \n \n \n");
        }
    }
}
