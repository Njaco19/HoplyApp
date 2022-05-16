package com.example.hoplyapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;


/**
 * This fragment prints all users in the local database, in a scroll view.
 * The purpose of this is to test the connection to either database. It is not a feature of the app.
 * If it prints every user in the JSON database, we know that we are successfully connected to it.
 */
public class AllUsers extends Fragment {

    //Empty public constructor required in a fragment.
    public AllUsers() {}

    /*
     * Uses a ForkJoinPool to add the names of every user in the local database to a list, and displays the list in a scroll view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);
        ForkJoinPool fjp = new ForkJoinPool();
        try {
            LinearLayout ll = view.findViewById(R.id.allUserLayout);
            FutureTask<List<String>> ftUser = new FutureTask<>(() -> MainActivity.ld.getDAO().getAllUserID());
            fjp.execute(ftUser);
            List<String> allUsersID = ftUser.get();
            for (String ID : allUsersID) {
                TextView user = new TextView(AllUsers.this.getActivity());
                user.setText(ID);
                ll.addView(user);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }
        return view;
    }
}
