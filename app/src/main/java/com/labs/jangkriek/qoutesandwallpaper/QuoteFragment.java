package com.labs.jangkriek.qoutesandwallpaper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labs.jangkriek.qoutesandwallpaper.adapter.CategoriesAdapter;
import com.labs.jangkriek.qoutesandwallpaper.model.Category;

import java.util.ArrayList;
import java.util.List;

public class QuoteFragment extends Fragment {

    private ProgressBar progressBar;
    private DatabaseReference dbCategories;
    private List<Category> categoryList;
    private RecyclerView recyclerView;
    private CategoriesAdapter categoriesAdapter;
    private int mNoOfColumns;

    public QuoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quote, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.pb);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recycler_view);

        mNoOfColumns = Utility.calculateNoOfColumns(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mNoOfColumns));

        categoryList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList);
        recyclerView.setAdapter(categoriesAdapter);

        dbCategories = FirebaseDatabase.getInstance().getReference("categories");
        dbCategories.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        String desc = ds.child("desc").getValue(String.class);
                        String thumb = ds.child("thumbnail").getValue(String.class);
                        String ig = ds.child("ig").getValue(String.class);
                        String fb = ds.child("fb").getValue(String.class);

                        Category cat = new Category(name, desc, thumb, ig, fb);
                        categoryList.add(cat);
                    }
                    categoriesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}