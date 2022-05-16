package com.example.hoplyapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;

/**
 * This fragment let's the user login to the app using their UserID
 */
public class LoginScreen extends Fragment {
    public static User thisUser; //This is the app's way of knowing which user is currently logged in.

    /*
     * Checks if there is at least one user in the local database with this id.
     * If that is the case, it sets the static attribute thisUser as that user, and navigates them to the front page.
     */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_screen, container, false);
        ForkJoinPool fjp = new ForkJoinPool();
        EditText userIDEdit = view.findViewById(R.id.LoginUserText);

        view.findViewById(R.id.LoginButton).setOnClickListener((view1) -> {
            String userID = userIDEdit.getText().toString();
            try {
                FutureTask<List<User>> ftUser = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllUsers());
                fjp.execute(ftUser);
                List<User> users = ftUser.get();
                // If a user with the given id exists, sets thisUser as that user and navigates to the front page.
                if (users.stream()
                        .filter(user -> user.id.equals(userID))
                        .count() != 0)
                {
                    thisUser = users.stream()
                            .filter(user -> user.id.equals(userID))
                            .findFirst()
                            .get();
                    Toast.makeText(LoginScreen.this.getActivity(), "Succesfully logged in", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(LoginScreen.this)
                            .navigate(R.id.action_loginScreen_to_frontPage);
                } else
                    Toast.makeText(LoginScreen.this.getActivity(), "UserID not found", Toast.LENGTH_SHORT).show();
            }catch (InterruptedException e){
                e.printStackTrace();
            }catch (ExecutionException e){
                e.printStackTrace();
            }
        });

        // Navigates back to FirstFragment.
        view.findViewById(R.id.button3).setOnClickListener((view1)->{
            NavHostFragment.findNavController(LoginScreen.this)
                    .navigate(R.id.action_loginScreen_to_FirstFragment);
        });

        return view;
    }
}
