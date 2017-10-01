package com.example.ank.digilib.Activities;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ank.digilib.Adapters.BooksAdapter;
import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyBooksActivity extends AppCompatActivity {

    private TextView isEmptyMarker;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private BooksAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chosenBooksDatabaseReference;
    private DatabaseReference booksDatabaseReference;
    private ValueEventListener valueEventListener;

    public static ArrayList<Book> bookList;
    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        isEmptyMarker = (TextView) findViewById(R.id.is_empty_marker);

        bookList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.book_list);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        chosenBooksDatabaseReference = firebaseDatabase.getReference().child("chosenBooks");
        booksDatabaseReference = firebaseDatabase.getReference().child("books");

        MyBooksActivity.FetchBookList fBL = new MyBooksActivity.FetchBookList();
        fBL.execute();
    }

    public class FetchBookList extends AsyncTask<Void,Void,ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(Void... params) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bookList.clear();
                    for(final DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if(snapshot.getKey().toString().equals(user.getUid())) {
                            String uid = user.getUid();
                            chosenBooksDatabaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1: dataSnapshot.getChildren()) {
                                        final String bookKey = (String) snapshot1.child("key").getValue();
                                        String genreName = (String) snapshot1.child("genreName").getValue();
                                        String purchaseType = (String) snapshot1.child("purchaseType").getValue();
                                        booksDatabaseReference.child(genreName).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot snapshot2: dataSnapshot.getChildren()) {
                                                    if(snapshot2.getKey().toString().equals(bookKey)) {
                                                        String name = (String) snapshot2.child("name").getValue();
                                                        String author = (String) snapshot2.child("author").getValue();
                                                        String salePrice = (String) snapshot2.child("salePrice").getValue();
                                                        String rentalPrice = (String) snapshot2.child("rentalPrice").getValue();
                                                        String coverImageURL = (String) snapshot2.child("coverImageURL").getValue();
                                                        String genreName = (String) snapshot2.child("genreName").getValue();
                                                        if(name != null) {
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

            chosenBooksDatabaseReference.addValueEventListener(valueEventListener);
            return null;
        }
    }

    void updateUI() {
        if(bookList.isEmpty()) {
            isEmptyMarker.setText("You havent bought or rented any books yet!");
        }
        else {
            isEmptyMarker.setText("");
        }
        adapter = new BooksAdapter(bookList);
        recyclerView.setAdapter(adapter);
    }
}
