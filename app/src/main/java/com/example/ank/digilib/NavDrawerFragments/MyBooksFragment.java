package com.example.ank.digilib.NavDrawerFragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ank.digilib.Adapters.BooksAdapter;
import com.example.ank.digilib.Adapters.GenresAdapter;
import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.Objects.Genre;
import com.example.ank.digilib.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by adityadesai on 02/09/17.
 */

public class MyBooksFragment extends android.support.v4.app.Fragment {

    private TextView isEmptyMarker;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private BooksAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference chosenBooksDatabaseReference;
    private DatabaseReference booksDatabaseReference;
    private ValueEventListener valueEventListener;

    public static ArrayList<Book> bookList;
    private String userKey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView =inflater.inflate(R.layout.featured_page,null);

        isEmptyMarker = (TextView) rootView.findViewById(R.id.is_empty_marker);

        bookList = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.genre_list);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        userDatabaseReference = firebaseDatabase.getReference().child("users");
        chosenBooksDatabaseReference = firebaseDatabase.getReference().child("users");
        booksDatabaseReference = firebaseDatabase.getReference().child("books");

        MyBooksFragment.FetchBookList fBL = new MyBooksFragment.FetchBookList();
        fBL.execute();

        return rootView;
    }

    public class FetchBookList extends AsyncTask<Void,Void,ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(Void... params) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if(snapshot.child("uid").getValue().toString().equals(user.getUid())) {
                            chosenBooksDatabaseReference.child(snapshot.getKey()).child("chosenBooks").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    bookList.clear();
                                    for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        final String key = (String) snapshot.child("key").getValue();
                                        String genreName = (String) snapshot.child("genreName").getValue();
                                        if(key != null) {
                                            booksDatabaseReference.child(genreName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot snapshot1: dataSnapshot.getChildren()) {
                                                        if(snapshot1.getKey().equals(key)) {
                                                            String name = (String) snapshot1.child("name").getValue();
                                                            String author = (String) snapshot1.child("author").getValue();
                                                            String salePrice = (String) snapshot1.child("salePrice").getValue();
                                                            String rentalPrice = (String) snapshot1.child("rentalPrice").getValue();
                                                            String coverImageURL = (String) snapshot1.child("coverImageURL").getValue();
                                                            String genreName = (String) snapshot1.child("genreName").getValue();
                                                            if(name != null) {
//                                                                Log.v("tag1", snapshot1.child("name").getValue().toString());
                                                                bookList.add(new Book(name, author, salePrice, rentalPrice, coverImageURL, genreName));
                                                            }
                                                        }
                                                    }
                                                    updateUI();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            userDatabaseReference.addValueEventListener(valueEventListener);
            return null;
        }
    }

    void updateUI() {
        Log.v("tag1", Integer.toString(bookList.size()));
        if (bookList.isEmpty()) {
            AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        adapter = new BooksAdapter(bookList);
        recyclerView.setAdapter(adapter);
    }
}
