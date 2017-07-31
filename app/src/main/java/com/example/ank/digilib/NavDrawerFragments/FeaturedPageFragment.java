package com.example.ank.digilib.NavDrawerFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ank.digilib.Adapters.GenresAdapter;
import com.example.ank.digilib.Objects.Genre;
import com.example.ank.digilib.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by adityadesai on 12/07/17.
 */

public class FeaturedPageFragment extends android.support.v4.app.Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private GenresAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private ArrayList<Genre> genreList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView =inflater.inflate(R.layout.featured_page,null);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("books");

        genreList = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.genre_list);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);


//        databaseReference.push().setValue(new Genre("Fantasy"));
//        databaseReference.push().setValue(new Genre("Mystery"));
//        databaseReference.push().setValue(new Genre("Non Fiction"));
//        databaseReference.push().setValue(new Genre("Educational"));

        FeaturedPageFragment.FetchGenreList fGL = new FeaturedPageFragment.FetchGenreList();
        fGL.execute();

//        databaseReference.push().setValue(new Book("Harry Potter and the Philospher's Stone", "J.K. Rowling", "300", "75"));
//        databaseReference.push().setValue(new Book("Harry Potter and the Chamber of Secrets", "J.K. Rowling", "300", "75"));
//        databaseReference.push().setValue(new Book("Harry Potter and the Prisoner of Azkaban", "J.K. Rowling", "300", "75"));
//        databaseReference.push().setValue(new Book("Harry Potter and the Goblet of Fire", "J.K. Rowling", "300", "75"));
//        databaseReference.push().setValue(new Book("Harry Potter and the Order of the Phoenix", "J.K. Rowling", "300", "75"));
//        databaseReference.push().setValue(new Book("Harry Potter and the Half Blood Prince", "J.K. Rowling", "300", "75"));
//        databaseReference.push().setValue(new Book("Harry Potter and the Deathly Hallows", "J.K. Rowling", "300", "75"));


        return rootView;
    }

    public class FetchGenreList extends AsyncTask<Void,Void,ArrayList<Genre>> {

        @Override
        protected ArrayList<Genre> doInBackground(Void... params) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    genreList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String name = (String) snapshot.child("name").getValue();
                        if(name != null) {
                            genreList.add(new Genre(name));
                        }
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            databaseReference.addValueEventListener(valueEventListener);
            return null;
        }
    }

    public void updateUI() {
        adapter = new GenresAdapter(genreList);
        recyclerView.setAdapter(adapter);
    }
}
