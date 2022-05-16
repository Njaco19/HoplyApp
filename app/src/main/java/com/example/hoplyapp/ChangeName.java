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

import java.util.concurrent.ForkJoinPool;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Fragment for updating the users name
 * For security reasons the users is asked to enter their ID as well as their new name
 * This feature is our advanced/fun feature.
 */
public class ChangeName extends Fragment {

    //Empty public constructor required in a fragment.
    public ChangeName() {}

    /*
     * Uses a ForkJoinPool to update the users name in the remote and local database.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_name, container, false);

        ForkJoinPool fjp = new ForkJoinPool();

        User thisUser = LoginScreen.thisUser; //An object of User representing the logged in user

        EditText userIDEdit = view.findViewById(R.id.CurrentUserId);
        EditText newNameEdit = view.findViewById(R.id.EditUsername);
        Button change = view.findViewById(R.id.change);

        change.setOnClickListener((view1) -> {


            // Getting the strings from the user.
            String newName = newNameEdit.getText().toString();
            String userID = userIDEdit.getText().toString();

            if(userID.equals(thisUser.id)){ // Checking if it's the correct userid entered.
                User newUser = thisUser;// Creates the same user and updates it's name.
                newUser.name = newName;

                fjp.execute(() -> MainActivity.ld.getDAO().updateUser(newUser)); // Updates the user in the local Database.

                // Updates the user in the remote database.
                Call<User> updateName = updateUser(userID, newUser);
                updateName.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()){ // If there was an error, this displays the error message.
                            Toast.makeText(ChangeName.this.getActivity(), response.code() + response.message(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(ChangeName.this.getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                Toast.makeText(ChangeName.this.getActivity(), "Username changed successfully", Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(ChangeName.this)
                        .navigate(R.id.action_ChangeName_to_FrontPage);
            } else {
                Toast.makeText(ChangeName.this.getActivity(), "Incorrect UserID", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigates back to the front page, without changing anything.
        Button cancel = view.findViewById(R.id.CancelUsername);
        cancel.setOnClickListener((view1) -> {
            NavHostFragment.findNavController(ChangeName.this)
                    .navigate(R.id.action_ChangeName_to_FrontPage);
        });

        return view;
    }

    /*
     * This method calls the remote database and adds the string "eq." to the userID for the Query to work,
     * this makes sure that the update method checks if the ID equals the current id stored in the database.
     */
    private Call<User> updateUser(String userID, User user){
        return MainActivity.remoteDB.updateUsername("eq." + userID, user);
    }
}
