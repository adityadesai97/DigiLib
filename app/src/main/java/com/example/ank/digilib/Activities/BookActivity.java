package com.example.ank.digilib.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ank.digilib.Objects.ChosenBook;
import com.example.ank.digilib.Objects.FeedEvent;
import com.example.ank.digilib.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference bookDatabaseReference;
    private DatabaseReference chosenBookDatabaseReference;
    private ValueEventListener valueEventListener;

    private Button buyButton;
    private Button rentButton;

    private String genreName;
    private String bookName;
    private String bookBuyPrice;
    private String bookRentPrice;
    private String bookKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        Intent i = getIntent();
        genreName = i.getStringExtra("genreName");
        bookName = i.getStringExtra("bookName");

        setTitle(bookName);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference().child("users");
        bookDatabaseReference = firebaseDatabase.getReference().child("books").child(genreName);
        chosenBookDatabaseReference = firebaseDatabase.getReference().child("chosenBooks");

        buyButton = (Button)findViewById(R.id.buy_button);
        rentButton = (Button)findViewById(R.id.rent_button);

        bookDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.child("name").getValue().toString().equals(bookName)) {
                        bookBuyPrice = snapshot.child("salePrice").getValue().toString();
                        bookRentPrice = snapshot.child("rentalPrice").getValue().toString();
                        bookKey = snapshot.getKey();
                    }
                }
                enableButtons();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void enableButtons() {
        buyButton.setEnabled(true);
        rentButton.setEnabled(true);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractCredits("buy");
            }
        });

        rentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractCredits("rent");
            }
        });
    }

    void subtractCredits(final String mode) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String formattedDate = df.format(c.getTime());
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if(snapshot.child("uid").getValue().toString().equals(user.getUid())) {
                        String key = snapshot.getKey();
                        String uid = (String) snapshot.child("uid").getValue();
                        int credits = Integer.parseInt((String) snapshot.child("credits").getValue());
                        if(mode.equals("buy")) {
                            if(credits >= Integer.parseInt(bookBuyPrice)) {
                                Map<String,Object> taskMap = new HashMap<String,Object>();
                                int newCredits = credits - Integer.parseInt(bookBuyPrice);
                                taskMap.put("credits", Integer.toString(newCredits));
                                userDatabaseReference.child(key).updateChildren(taskMap);
                                userDatabaseReference.child(key).child("activity").push().setValue(new FeedEvent(uid, bookKey, mode, formattedDate));
                                chosenBookDatabaseReference.child(uid).push().setValue(new ChosenBook(bookKey, genreName, "buy"));
                                Toast.makeText(BookActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                                buyButton.setEnabled(false);
                                rentButton.setEnabled(false);
                            }
                            else {
                                Toast.makeText(BookActivity.this, "You do not have enough credits to buy this", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(mode.equals("rent")) {
                            if(credits >= Integer.parseInt(bookRentPrice)) {
                                Map<String,Object> taskMap = new HashMap<String,Object>();
                                int newCredits = credits - Integer.parseInt(bookRentPrice);
                                taskMap.put("credits", Integer.toString(newCredits));
                                userDatabaseReference.child(key).updateChildren(taskMap);
                                chosenBookDatabaseReference.child(uid).push().setValue(new ChosenBook(bookKey, genreName, "rent"));
                                Toast.makeText(BookActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                                buyButton.setEnabled(false);
                                rentButton.setEnabled(false);
                            }
                            else {
                                Toast.makeText(BookActivity.this, "You do not have enough credits to rent this", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
