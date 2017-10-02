package com.example.ank.digilib.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.ank.digilib.Adapters.FeedAdapter;
import com.example.ank.digilib.Adapters.GenresAdapter;
import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.Objects.ChosenBook;
import com.example.ank.digilib.Objects.FeedEvent;
import com.example.ank.digilib.Objects.Genre;
import com.example.ank.digilib.Objects.Request;
import com.example.ank.digilib.Objects.User;
import com.example.ank.digilib.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static io.fabric.sdk.android.services.concurrency.AsyncTask.init;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN=1;

    private SharedPreferences.Editor editor;
    SharedPreferences prefs;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference genreDatabaseReference;
    private DatabaseReference chosenBooksDatabaseReference;
    private ValueEventListener valueEventListener;

    private RecyclerView genreRecyclerView;
    private RecyclerView feedRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private GenresAdapter genresAdapter;
    private FeedAdapter feedAdapter;

    private ArrayList<Genre> genreList;
    private ArrayList<FeedEvent> feedList;
    private String startCredits = "10000";
    private int userFoundFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference().child("users");
        genreDatabaseReference = firebaseDatabase.getReference().child("books");
        chosenBooksDatabaseReference = firebaseDatabase.getReference().child("chosenBooks");

        genreList = new ArrayList<>();
        feedList = new ArrayList<>();

        genreRecyclerView = (RecyclerView)findViewById(R.id.genre_list);
        gridLayoutManager = new GridLayoutManager(this, 2);
        genreRecyclerView.setLayoutManager(gridLayoutManager);
        feedRecyclerView = (RecyclerView)findViewById(R.id.feed_list);
        linearLayoutManager = new LinearLayoutManager(this);
        feedRecyclerView.setLayoutManager(linearLayoutManager);

        final com.github.clans.fab.FloatingActionMenu menuFab = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.menu_fab);
        menuFab.setIconAnimated(false);
        menuFab.setClosedOnTouchOutside(true);

        com.github.clans.fab.FloatingActionButton myBooksFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.my_books_fab);
        myBooksFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFab.close(false);
                Intent i = new Intent(v.getContext(), MyBooksActivity.class);
                v.getContext().startActivity(i);
            }
        });

        com.github.clans.fab.FloatingActionButton teamFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.team_fab);
        teamFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFab.close(true);
                Toast.makeText(v.getContext(), "This feature is currently unavailable", Toast.LENGTH_SHORT).show();
            }
        });

        com.github.clans.fab.FloatingActionButton myFriendsFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.friends_fab);
        myFriendsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFab.close(false);
                Intent i = new Intent(v.getContext(), MyFriendsActivity.class);
                v.getContext().startActivity(i);
            }
        });

        LinearLayout getMoreCredits = (LinearLayout) findViewById(R.id.get_more_button);
        getMoreCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BuyCreditsActivity.class);
                startActivity(i);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    userDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.child("uid").getValue().toString().equals(user.getUid())) {
                                    ImageView profilePicture = (ImageView) findViewById(R.id.profile_picture);
                                    Glide.with(profilePicture.getContext()).load(snapshot.child("profile_url").getValue().toString()).into(profilePicture);

                                    TextView username = (TextView) findViewById(R.id.username);
                                    username.setText("Welcome! " + snapshot.child("name").getValue().toString().split("\\s+")[0]);

                                    TextView balance = (TextView) findViewById(R.id.credit_balance);
                                    balance.setText(snapshot.child("credits").getValue().toString() + " DigiCreds");
                                    userFoundFlag = 1;
                                }
                            }
                            if(userFoundFlag == 0) {
                                userDatabaseReference.push().setValue(new User(user.getDisplayName(), user.getUid(), user.getEmail(), user.getPhotoUrl().toString(), startCredits));
                                chosenBooksDatabaseReference.child(user.getUid()).push().setValue(new ChosenBook());

                                ImageView profilePicture = (ImageView) findViewById(R.id.profile_picture);
                                Glide.with(profilePicture.getContext()).load(user.getPhotoUrl().toString()).into(profilePicture);

                                TextView username = (TextView) findViewById(R.id.username);
                                username.setText("Welcome! " + user.getDisplayName().toString().split("\\s+")[0]);

                                TextView balance = (TextView) findViewById(R.id.credit_balance);
                                balance.setText(startCredits + " DigiCreds");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    MainActivity.FetchGenreList fGL = new MainActivity.FetchGenreList();
                    fGL.execute();

                    MainActivity.FetchFeedList fFL = new MainActivity.FetchFeedList();
                    fFL.execute();
                }
                else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
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
                        String backgroundImage = (String) snapshot.child("backgroundImage").getValue();
                        if(name != null) {
                            genreList.add(new Genre(name, backgroundImage));
                        }
                    }
                    updateUI1();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            genreDatabaseReference.addValueEventListener(valueEventListener);
            return null;
        }
    }

    public class FetchFeedList extends AsyncTask<Void,Void,ArrayList<FeedEvent>> {

        @Override
        protected ArrayList<FeedEvent> doInBackground(Void... params) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    feedList.clear();
                    for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if(snapshot.child("uid").getValue().equals(user.getUid()) && snapshot.hasChild("friends")) {
                            userDatabaseReference.child(snapshot.getKey()).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1: dataSnapshot.getChildren()) {
                                        final String friendUid = (String)snapshot1.child("uid").getValue();
                                        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(final DataSnapshot snapshot2: dataSnapshot.getChildren()) {
                                                    if(snapshot2.child("uid").getValue().toString().equals(friendUid)) {
                                                        userDatabaseReference.child(snapshot2.getKey()).child("activity").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for(DataSnapshot snapshot3: dataSnapshot.getChildren()) {
                                                                    String genreName = (String)snapshot3.child("genreName").getValue();
                                                                    String name = (String)snapshot3.child("name").getValue();
                                                                    String profilepictureURL = (String)snapshot3.child("profile_id").getValue();
                                                                    String bookId = (String)snapshot3.child("bookId").getValue();
                                                                    String purchaseType = (String)snapshot3.child("purchaseType").getValue();
                                                                    String timestamp = (String)snapshot3.child("timestamp").getValue();
                                                                    Calendar c = Calendar.getInstance();
                                                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                    int timeDifference = 0;
                                                                    try {
                                                                        Date givenDate = df.parse(timestamp);
                                                                        Date currentDate = c.getTime();
                                                                        timeDifference = (int)(currentDate.getTime() - givenDate.getTime())/1000/60;
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    if(timeDifference <= 60) {
                                                                        feedList.add(new FeedEvent(genreName, name, profilepictureURL, bookId, purchaseType, timestamp));
                                                                    }
                                                                    else {
                                                                        userDatabaseReference.child(snapshot2.getKey()).child("activity").child(snapshot3.getKey()).removeValue();
                                                                    }
                                                                }
                                                                updateUI2();
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

    public void updateUI1() {
        genresAdapter = new GenresAdapter(genreList);
        genreRecyclerView.setAdapter(genresAdapter);
    }

    public void updateUI2() {
        feedAdapter = new FeedAdapter(feedList);
        feedRecyclerView.setAdapter(feedAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
