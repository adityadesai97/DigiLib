package com.example.ank.digilib.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ank.digilib.Adapters.BooksAdapter;
import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private BooksAdapter adapter;

    private String genreName;
    public static ArrayList<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        Intent i = getIntent();
        genreName = i.getStringExtra("genreName");

        setTitle(genreName);

        bookList = new ArrayList<>();

        recyclerView = (RecyclerView)findViewById(R.id.book_list);
        gridLayoutManager=new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("books").child(genreName);

        BookListActivity.FetchBookList fBL = new BookListActivity.FetchBookList();
        fBL.execute();
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
                        String salePrice = (String) snapshot.child("salePrice").getValue();
                        String rentalPrice = (String) snapshot.child("rentalPrice").getValue();
                        String coverImageURL = (String) snapshot.child("coverImageURL").getValue();
                        String genreName = (String) snapshot.child("genreName").getValue();
                        if(name != null) {
                            bookList.add(new Book(name, author, salePrice, rentalPrice, coverImageURL, genreName));
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
        TextView isEmptyMarker = (TextView)findViewById(R.id.is_empty_marker);
        if(bookList.isEmpty()) {
            isEmptyMarker.setText("Sorry, there are no books currently avaialble in this genre.");
        }
        adapter = new BooksAdapter(bookList);
        recyclerView.setAdapter(adapter);
    }
}
