package com.example.hoplyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment for creating a new user, with a given name and id.
 */

public class SecondFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        EditText usernameEdit = view.findViewById(R.id.UsernameText);
        EditText userIDEdit = view.findViewById(R.id.UserIDText);

        // When the user clicks on the Register button, it creates a new user.
        view.findViewById(R.id.RegisterButton).setOnClickListener((view1) -> {
                ForkJoinPool fjp = new ForkJoinPool();

                String username = usernameEdit.getText().toString();
                String userID = userIDEdit.getText().toString();

                try {
                    FutureTask<List<String>> ftUserID = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllUserID());
                    fjp.execute(ftUserID);
                    List<String> usersID = ftUserID.get();// Gets all the userIDs from the local database.

                    if (usersID.contains(userID)) // checks if the given id already exists.
                        Toast.makeText(SecondFragment.this.getActivity(), "User already exists", Toast.LENGTH_SHORT).show();
                    else {

                        // Creates a new User with a given name and id,
                        User user = new User();
                        user.id = userID;
                        user.name = username;
                        user.stamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()).format((DateTimeFormatter.ISO_OFFSET_DATE_TIME));

                        fjp.execute(() -> MainActivity.ld.getDAO().insertUser(user)); // Inserts the user into the local database.

                        // Inserts the user into the remote database.
                        Call<User> insertUser = MainActivity.remoteDB.insertUser(userID, username, user.stamp);
                        insertUser.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if(!response.isSuccessful()){ // If there was an error, this displays the error message
                                    Toast.makeText(SecondFragment.this.getActivity(), response.code() + response.message(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) { }
                        });

                        Toast.makeText(SecondFragment.this.getActivity(), "User successfully created", Toast.LENGTH_SHORT).show();

                        // Navigates to the LoginScreen.
                        NavHostFragment.findNavController(SecondFragment.this)
                                .navigate(R.id.action_SecondFragment_to_loginScreen);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (ExecutionException e){
                    e.printStackTrace();
                }

        });

        //Navigates back to the FirstFragment.
        view.findViewById(R.id.button_second).setOnClickListener((view1) -> {
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);

        });

        return view;
    }
}
