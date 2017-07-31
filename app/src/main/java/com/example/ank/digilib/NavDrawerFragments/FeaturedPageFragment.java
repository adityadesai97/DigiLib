package com.example.ank.digilib.NavDrawerFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ank.digilib.Adapters.FeaturedBooksAdapter;
import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by adityadesai on 12/07/17.
 */

public class FeaturedPageFragment extends android.support.v4.app.Fragment {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FeaturedBooksAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private ArrayList<Book> bookList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView =inflater.inflate(R.layout.featured_page,null);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("books");

        bookList = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.featured_book_list);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        FeaturedPageFragment.FetchBookList fBL = new FeaturedPageFragment.FetchBookList();
        fBL.execute();

        return rootView;
    }

    public class FetchBookList extends AsyncTask<Void,Void,ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(Void... params) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bookList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String name = (String) snapshot.child("name").getValue();
                        String author = (String) snapshot.child("author").getValue();
                        String category = (String) snapshot.child("category").getValue();
                        if(name != null) {
                            bookList.add(new Book(name, author, category));
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
        adapter = new FeaturedBooksAdapter(bookList,getActivity());
        recyclerView.setAdapter(adapter);
    }
}
