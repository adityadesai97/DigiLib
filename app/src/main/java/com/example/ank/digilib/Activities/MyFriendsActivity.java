package com.example.ank.digilib.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ank.digilib.Adapters.FriendsAdapter;
import com.example.ank.digilib.Adapters.RequestsAdapter;
import com.example.ank.digilib.Objects.User;
import com.example.ank.digilib.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFriendsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private TextView isEmptyMarker;
    LinearLayoutManager linearLayoutManager;
    FriendsAdapter adapter;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser user;
    ValueEventListener valueEventListener;

    ArrayList<User> friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        isEmptyMarker = (TextView) findViewById(R.id.is_empty_marker);

        friendsList = new ArrayList<>();

        recyclerView = (RecyclerView)findViewById(R.id.friends_list);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        MyFriendsActivity.FetchFriendsList fFL = new MyFriendsActivity.FetchFriendsList();
        fFL.execute();

        final com.github.clans.fab.FloatingActionMenu menuFab = (com.github.clans.fab.FloatingActionMenu) findViewById(R.id.menu_fab);
        menuFab.setIconAnimated(false);
        menuFab.setClosedOnTouchOutside(true);

        com.github.clans.fab.FloatingActionButton requestsFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.requests_fab);
        requestsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFab.close(false);
                Intent i = new Intent(v.getContext(), RequestsActivity.class);
                v.getContext().startActivity(i);
            }
        });
        com.github.clans.fab.FloatingActionButton AddFriendFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.add_friend_fab);
        AddFriendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuFab.close(false);
                Intent i = new Intent(v.getContext(), AddFriendActivity.class);
                v.getContext().startActivity(i);
            }
        });
    }

    public class FetchFriendsList extends AsyncTask<Void,Void,ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(Void... params) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    friendsList.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if(snapshot.child("uid").getValue().equals(user.getUid())) {
                            databaseReference.child(snapshot.getKey()).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                        String name = (String) snapshot1.child("name").getValue();
                                        String uid = (String) snapshot1.child("uid").getValue();
                                        String email = (String) snapshot1.child("mail_id").getValue();
                                        String profilepictureURL = (String) snapshot1.child("profile_url").getValue();
                                        if(name != null) {
                                            friendsList.add(new User(name, uid, email, profilepictureURL, null));
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
            };

            databaseReference.addValueEventListener(valueEventListener);
            return null;
        }
    }

    public void updateUI() {
        if(friendsList.isEmpty()) {
            isEmptyMarker.setText("You don't have any friends yet!");
        }
        else {
            isEmptyMarker.setText("");
        }
        adapter = new FriendsAdapter(friendsList);
        recyclerView.setAdapter(adapter);
    }
}
