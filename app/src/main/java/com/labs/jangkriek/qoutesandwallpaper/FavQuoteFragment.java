package com.labs.jangkriek.qoutesandwallpaper;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labs.jangkriek.qoutesandwallpaper.adapter.FavAdapter;
import com.labs.jangkriek.qoutesandwallpaper.fragment.SettingFragment;
import com.labs.jangkriek.qoutesandwallpaper.model.Wallpaper;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavQuoteFragment extends Fragment {


    public FavQuoteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav_quote, container, false);
    }

    List<Wallpaper> favWallpaper;
    RecyclerView recyclerView;
    ProgressBar pb;
    FavAdapter adapter;
    DatabaseReference dbFav;
    int mNoOfColumns;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favWallpaper = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view3);
        pb = view.findViewById(R.id.pb3);
        adapter = new FavAdapter(getActivity(), favWallpaper);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNoOfColumns = Utility.calculateNoOfColumns(getActivity());

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mNoOfColumns));
        recyclerView.setAdapter(adapter);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_main, new SettingFragment())
                    .commit();
            return;
        }

        dbFav = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favourites");

        pb.setVisibility(View.VISIBLE);

        dbFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pb.setVisibility(View.INVISIBLE);
                for(DataSnapshot cat : dataSnapshot.getChildren()){
                    for(DataSnapshot wall : cat.getChildren()){
                        String id = wall.getKey();
                        String title = wall.child("title").getValue(String.class);
                        String desc = wall.child("desc").getValue(String.class);
                        String ig = wall.child("ig").getValue(String.class);
                        String fb = wall.child("fb").getValue(String.class);
                        String url = wall.child("url").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, ig, fb, url, cat.getKey());
                        w.isFav = true;
                        favWallpaper.add(w);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
