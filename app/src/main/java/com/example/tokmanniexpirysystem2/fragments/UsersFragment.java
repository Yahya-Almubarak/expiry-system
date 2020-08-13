package com.example.tokmanniexpirysystem2.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.User;
import com.example.tokmanniexpirysystem2.listadapters.UserRecyclerviewAdapter;

import java.util.List;


public class UsersFragment extends Fragment {
    RecyclerView recyclerView;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.users_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        getUsers();

        return view;
    }

    private void getUsers() {
        class GetUsers extends AsyncTask<Void, Void, List<User>> {

            @Override
            protected List<User> doInBackground(Void... voids) {
                List<User> usersList = DatabaseClient
                        .getInstance(getActivity().getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getAllAsList();

                return usersList;
            }

            @Override
            protected void onPostExecute(List<User> users) {
                super.onPostExecute(users);
                Log.d("Users ", String.valueOf(users.size()));
                UserRecyclerviewAdapter adapter = new UserRecyclerviewAdapter(getActivity(), users);
                recyclerView.setAdapter(adapter);
            }
        }

        GetUsers gu = new GetUsers();
        gu.execute();
    }
}
