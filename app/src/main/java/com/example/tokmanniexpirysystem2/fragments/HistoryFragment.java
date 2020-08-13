package com.example.tokmanniexpirysystem2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.History;
import com.example.tokmanniexpirysystem2.entities.User;
import com.example.tokmanniexpirysystem2.listadapters.HistoryRecyclerviewAdapter;
import com.example.tokmanniexpirysystem2.listadapters.UserRecyclerviewAdapter;

import java.util.Collections;
import java.util.List;


public class HistoryFragment extends Fragment {
    RecyclerView recyclerView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.history_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        getHistory();

        return view;
    }

    private void getHistory() {
        class GetHistory extends AsyncTask<Void, Void, List<History>> {

            @Override
            protected List<History> doInBackground(Void... voids) {
                List<History> historyList = DatabaseClient
                        .getInstance(getActivity().getApplicationContext())
                        .getAppDatabase()
                        .historyDao()
                        .getAll();
                Collections.reverse(historyList);
                return historyList;
            }

            @Override
            protected void onPostExecute(List<History> history) {
                super.onPostExecute(history);
                HistoryRecyclerviewAdapter adapter = new HistoryRecyclerviewAdapter(getActivity(), history);
                recyclerView.setAdapter(adapter);
            }
        }

        GetHistory gh = new GetHistory();
        gh.execute();
    }
}
