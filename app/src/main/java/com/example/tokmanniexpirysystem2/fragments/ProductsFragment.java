package com.example.tokmanniexpirysystem2.fragments;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tokmanniexpirysystem2.alarm.AlarmHelper;
import com.example.tokmanniexpirysystem2.listadapters.ProductRecyclerviewAdapter;
import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.Product;

import java.util.Collections;
import java.util.List;

public class ProductsFragment extends Fragment {

    RecyclerView recyclerView;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.products_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        getProducts();

        return view;
    }



    private void getProducts() {
        class GetProducts extends AsyncTask<Void, Void, List<Product>> {

            @Override
            protected List<Product> doInBackground(Void... voids) {
                List<Product> productsList = DatabaseClient
                        .getInstance(getActivity().getApplicationContext())
                        .getAppDatabase()
                        .productDao()
                        .getAll();

                return productsList;
            }

            @Override
            protected void onPostExecute(List<Product> products) {
                super.onPostExecute(products);
                Collections.sort(products, Product.compareAlarmDate);
                ProductRecyclerviewAdapter adapter = new ProductRecyclerviewAdapter(getActivity(), products);
                recyclerView.setAdapter(adapter);
            }
        }

        GetProducts gp = new GetProducts();
        gp.execute();
    }


}
