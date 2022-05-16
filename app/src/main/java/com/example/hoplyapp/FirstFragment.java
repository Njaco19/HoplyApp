package com.example.hoplyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

/**
 * This fragment is the first screen of the app.
 * This fragment only consists of 3 buttons used for navigation
 * The button to show all users has been disabled
 */
public class FirstFragment extends Fragment {
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        // Navigates to the AllUsers fragment
        view.findViewById(R.id.AllUserBtn).setOnClickListener((view1) -> {
            //Toast.makeText(FirstFragment.this.getActivity(), "Feature disabled", Toast.LENGTH_SHORT).show(); // Comment this out when the feature is enabled
            NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_MainScreen_to_allUsers); // Remove "//" to enable this feature
        });

        // Navigates to the LoginScreen
        view.findViewById(R.id.Button_Login).setOnClickListener((view1) -> {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_loginScreen);
        });

        // Navigates to the SecondFragment
        view.findViewById(R.id.button_first).setOnClickListener((view1) -> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);
        });

        return view;
    }
}
